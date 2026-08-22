# What this zip does

Two EC2 instances, both public, nothing else:

1. **jenkins-controller** — Jenkins is installed and fully configured by JCasC
   (admin user, and the `java-app-deploy` pipeline job) with no manual setup
   wizard. Builds run **on this same instance** (no separate agent EC2).
   It also has Ansible installed, and already has the SSH key + inventory
   needed to reach the app server.
2. **java-app-server** — just Java installed, waiting for Jenkins to deploy
   the jar to it via Ansible and run it as a systemd service on port `9090`
   (kept different from Jenkins' own port 8080, on purpose).

No Nexus. Jenkins builds the jar and copies it straight to the app server
with Ansible.

## One-time setup

1. Unzip this file.
2. Copy `Jenkinsfile` from this zip into the **root of your app's git repo**,
   overwriting the existing one (the old one pushed to Nexus and expected a
   separate Ansible-controller box — this one doesn't need either).
3. Push that repo to GitHub/GitLab. Keep the repo **public**, or see the
   note at the bottom if it's private.
4. `cd terraform`
5. Create a `terraform.tfvars` file:
   ```hcl
   git_repo_url = "https://github.com/<you>/<your-repo>.git"
   git_branch   = "main"
   # optional, defaults shown:
   aws_region        = "ap-south-1"
   allowed_ssh_cidr  = "0.0.0.0/0"   # lock this to your IP/32 if you can
   app_port          = 9090
   ```
6. Make sure your AWS credentials are set (`aws configure`, or env vars).
7. Run:
   ```bash
   terraform init
   terraform apply
   ```
8. Wait ~4-5 minutes after apply finishes for Jenkins to finish installing
   plugins and boot up.
9. Get the outputs:
   ```bash
   terraform output jenkins_url
   terraform output jenkins_admin_user
   terraform output -raw jenkins_admin_password
   ```
10. Open `jenkins_url`, log in with those credentials. The job
    **java-app-deploy** already exists — click **Build Now**.

That single build will: checkout your repo, build/test/package the jar,
copy it to the app server with Ansible, start it as a `systemd` service on
port `9090`, and health-check it.

11. Open `terraform output app_url` — your app is live there.

## Day-to-day use

Just push code to your git repo, then click **Build Now** on the
`java-app-deploy` job (or add a webhook/poll-SCM trigger in Jenkins if you
want it fully hands-off — not set up by default to keep this simple).

## Files in this zip

```
terraform/
  main.tf                         # VPC lookup, key pair, security groups, both EC2s
  variables.tf                    # git_repo_url etc.
  outputs.tf                      # jenkins_url, app_url, admin password, ssh key path
  templates/
    jenkins_user_data.sh.tpl      # installs Jenkins/Java/Maven/Ansible, drops JCasC + plugins, starts Jenkins
    app_user_data.sh.tpl          # installs Java on the app server
    casc.yaml.tpl                 # JCasC: admin user, SSH credential, auto-created pipeline job
    deploy.yml                    # Ansible playbook: copies jar, templates systemd unit, (re)starts service
    java-app.service.j2           # systemd unit template (port is templated in)
Jenkinsfile                       # replaces the one in your app repo — no Nexus, deploys via Ansible directly
```

Terraform also writes `terraform/deployer-key.pem` locally after apply —
that's the SSH key for both instances, generated automatically. Same key
is embedded into the Jenkins controller so it can Ansible into the app
server without you doing anything.

## Notes

- Everything is intentionally public (`0.0.0.0/0`) for simplicity, per your
  ask. Tighten `allowed_ssh_cidr` and the app-port ingress in
  `variables.tf`/`main.tf` when you're done testing.
- Private git repo: add a Jenkins credential for it via the Jenkins UI
  (Manage Jenkins → Credentials) and reference its ID in the `git { }`
  block inside `casc.yaml.tpl`, then re-run `terraform apply` (or just
  edit the job in the UI once — JCasC won't fight you on manual job edits
  unless you reload the config).
- `terraform destroy` tears down both instances when you're done.
