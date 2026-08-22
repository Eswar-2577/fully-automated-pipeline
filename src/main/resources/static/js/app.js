// Drives the interactive parts of the page:
// 1) fetches live data from GET /api/profile (served by ProfileController)
// 2) renders Skills / Projects / Education / Certifications from that data
// 3) wires up tab navigation, accordion project cards, animated skill bars,
//    animated stat counters, and scroll-reveal effects.

document.addEventListener("DOMContentLoaded", () => {
  wireNav();
  loadProfile();
});

function wireNav() {
  const buttons = document.querySelectorAll(".nav-btn");
  const sections = document.querySelectorAll("main > section");

  buttons.forEach((btn) => {
    btn.addEventListener("click", () => {
      const target = btn.dataset.target;
      sections.forEach((s) => s.classList.toggle("hidden", s.id !== target));
      buttons.forEach((b) => b.classList.toggle("active", b === btn));
      window.scrollTo({ top: 0, behavior: "smooth" });
    });
  });
}

async function loadProfile() {
  try {
    const res = await fetch("/api/profile");
    if (!res.ok) throw new Error("Request failed: " + res.status);
    const data = await res.json();

    renderStats(data);
    renderSkills(data.skills);
    renderProjects(data.projects);
    renderEducation(data.education);
    renderCertifications(data.certifications);

    animateSkillBars();
    observeReveal();
  } catch (err) {
    console.error("Could not load /api/profile — showing static content only.", err);
  }
}

function renderStats(data) {
  const el = document.getElementById("stats");
  if (!el) return;
  const skillCount = Object.values(data.skills).reduce((sum, arr) => sum + arr.length, 0);
  const stats = [
    { num: data.projects.length, label: "Projects" },
    { num: Object.keys(data.skills).length, label: "Skill Areas" },
    { num: skillCount, label: "Tools & Tech" },
    { num: data.certifications.length, label: "Certifications" },
  ];
  el.innerHTML = stats
    .map(
      (s) => `
      <div class="stat-card reveal">
        <div class="stat-num" data-count="${s.num}">0</div>
        <div class="stat-label">${s.label}</div>
      </div>`
    )
    .join("");

  el.querySelectorAll("[data-count]").forEach((node) => {
    const target = parseInt(node.dataset.count, 10);
    let current = 0;
    const step = Math.max(1, Math.ceil(target / 30));
    const timer = setInterval(() => {
      current += step;
      if (current >= target) {
        node.textContent = target;
        clearInterval(timer);
      } else {
        node.textContent = current;
      }
    }, 30);
  });
}

function renderSkills(skills) {
  const el = document.getElementById("skills-grid");
  if (!el) return;

  el.innerHTML = Object.entries(skills)
    .map(([category, items]) => {
      const rows = items
        .map((item) => {
          const level = 70 + Math.floor(Math.random() * 26); // visual only
          return `
            <div class="skill-item">
              <div class="name">${item}</div>
              <div class="bar-track"><div class="bar-fill" data-level="${level}"></div></div>
            </div>`;
        })
        .join("");
      return `
        <div class="skill-cat card reveal">
          <h4>${category}</h4>
          ${rows}
        </div>`;
    })
    .join("");
}

function animateSkillBars() {
  document.querySelectorAll(".bar-fill").forEach((bar) => {
    requestAnimationFrame(() => {
      bar.style.width = bar.dataset.level + "%";
    });
  });
}

function renderProjects(projects) {
  const el = document.getElementById("projects-list");
  if (!el) return;

  el.innerHTML = projects
    .map(
      (p, i) => `
      <div class="card project-card reveal" data-index="${i}">
        <div class="project-head">
          <div>
            <h4>${p.name}</h4>
            <div class="project-stack">${p.stack}</div>
          </div>
          <span class="chevron">&#9660;</span>
        </div>
        <div class="project-body">
          <ul>${p.highlights.map((h) => `<li>${h}</li>`).join("")}</ul>
        </div>
      </div>`
    )
    .join("");

  el.querySelectorAll(".project-card").forEach((card) => {
    card.addEventListener("click", () => card.classList.toggle("open"));
  });
}

function renderEducation(education) {
  const el = document.getElementById("education-timeline");
  if (!el) return;

  el.innerHTML = education
    .map(
      (e) => `
      <div class="timeline-item reveal">
        <h4>${e.institution}</h4>
        <div class="period">${e.period} · ${e.location}</div>
        <div class="detail">${e.degree} — ${e.detail}</div>
      </div>`
    )
    .join("");
}

function renderCertifications(certifications) {
  const el = document.getElementById("certifications-list");
  if (!el) return;

  el.innerHTML = certifications
    .map(
      (c) => `
      <div class="card cert-card reveal">
        <div class="cert-icon">&#127942;</div>
        <div>
          <h4 style="margin:0 0 4px;">${c.title}</h4>
          <div class="project-stack">${c.provider} · ${c.period}</div>
          <div class="detail" style="margin-top:6px;color:var(--text-dim);font-size:0.88rem;">${c.detail}</div>
        </div>
      </div>`
    )
    .join("");
}

function observeReveal() {
  const items = document.querySelectorAll(".reveal");
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("in");
          observer.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.1 }
  );
  items.forEach((item) => observer.observe(item));
}
