/* ================================================================
   AOPE REAL ESTATE - DARK MODE TOGGLE
   ================================================================ */
(function () {
  const DARK_KEY   = 're_dark_mode';
  const toggle     = document.getElementById('darkModeToggle');
  const icon       = document.getElementById('darkModeIcon');
  const stylesheet = document.getElementById('darkModeStylesheet');

  function applyDarkMode(on) {
    if (on) {
      document.body.classList.add('dark-mode');
      if (stylesheet) stylesheet.disabled = false;
      if (icon)       { icon.className = 'bi bi-sun-fill'; }
      if (toggle)     toggle.title = 'Switch to light mode';
    } else {
      document.body.classList.remove('dark-mode');
      if (stylesheet) stylesheet.disabled = true;
      if (icon)       { icon.className = 'bi bi-moon-fill'; }
      if (toggle)     toggle.title = 'Switch to dark mode';
    }
    localStorage.setItem(DARK_KEY, on ? '1' : '0');
  }

  // Load saved preference
  const saved = localStorage.getItem(DARK_KEY);
  applyDarkMode(saved === '1');

  // Toggle on click
  if (toggle) {
    toggle.addEventListener('click', function () {
      applyDarkMode(!document.body.classList.contains('dark-mode'));
    });
  }
})();