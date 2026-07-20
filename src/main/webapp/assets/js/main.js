/* ================================================================
   AOPE REAL ESTATE - MAIN JAVASCRIPT
   ================================================================ */

'use strict';

/* ----------------------------------------------------------------
   1. NAVBAR - Add scrolled class on scroll
   ---------------------------------------------------------------- */
(function () {
  const navbar = document.getElementById('mainNavbar');
  if (!navbar) return;
  window.addEventListener('scroll', function () {
    if (window.scrollY > 20) {
      navbar.classList.add('scrolled');
    } else {
      navbar.classList.remove('scrolled');
    }
  });
})();

/* ----------------------------------------------------------------
   2. WISHLIST - Toggle save property (AJAX)
   ---------------------------------------------------------------- */
function toggleWishlist(btn, propertyId) {
  const ctx = document.body.dataset.ctx || '';
  fetch(ctx + '/user/wishlist', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: 'propertyId=' + propertyId
  })
  .then(res => res.json())
  .then(data => {
    if (data.status === 'saved') {
      btn.classList.add('saved');
      btn.title = 'Remove from wishlist';
      btn.querySelector('i').className = 'bi bi-heart-fill';
      showToast('Property saved to wishlist!', 'success');
    } else if (data.status === 'removed') {
      btn.classList.remove('saved');
      btn.title = 'Save to wishlist';
      btn.querySelector('i').className = 'bi bi-heart';
      showToast('Removed from wishlist.', 'info');
    } else if (data.status === 'login_required') {
      window.location = ctx + '/auth/login.jsp';
    }
  })
  .catch(() => showToast('Something went wrong. Please try again.', 'error'));
}

/* ----------------------------------------------------------------
   3. COMPARE - Add/remove property to comparison list
   ---------------------------------------------------------------- */
const CompareManager = (function () {
  const MAX  = 4;
  const KEY  = 're_compare';
  let ids    = JSON.parse(localStorage.getItem(KEY) || '[]');

  function save() { localStorage.setItem(KEY, JSON.stringify(ids)); }

  function add(id, title) {
    id = parseInt(id);
    if (ids.length >= MAX) {
      showToast('You can only compare up to ' + MAX + ' properties.', 'warning');
      return false;
    }
    if (ids.includes(id)) {
      showToast('Already added to comparison.', 'info');
      return false;
    }
    ids.push(id);
    save();
    updateCompareBar();
    showToast('"' + title + '" added to comparison.', 'success');
    return true;
  }

  function remove(id) {
    id  = parseInt(id);
    ids = ids.filter(x => x !== id);
    save();
    updateCompareBar();
  }

  function clear() { ids = []; save(); updateCompareBar(); }

  function getIds() { return [...ids]; }

  function updateCompareBar() {
    const bar   = document.getElementById('compareBar');
    const count = document.getElementById('compareCount');
    if (!bar) return;
    if (ids.length > 0) {
      bar.classList.add('show');
      if (count) count.textContent = ids.length;
    } else {
      bar.classList.remove('show');
    }
  }

  function navigateToCompare() {
    const ctx = document.body.dataset.ctx || '';
    if (ids.length < 2) {
      showToast('Please select at least 2 properties to compare.', 'warning');
      return;
    }
    window.location = ctx + '/properties/compare-properties.jsp?ids=' + ids.join(',');
  }

  // Initialize on load
  document.addEventListener('DOMContentLoaded', updateCompareBar);

  return { add, remove, clear, getIds, navigateToCompare };
})();

/* ----------------------------------------------------------------
   4. SEARCH SUGGESTIONS (AJAX autocomplete)
   ---------------------------------------------------------------- */
(function () {
  const input = document.getElementById('heroSearchInput');
  if (!input) return;

  const dropdown = document.createElement('div');
  dropdown.className  = 're-search-suggestions';
  dropdown.innerHTML  = '';
  input.parentNode.style.position = 'relative';
  input.parentNode.appendChild(dropdown);

  let debounceTimer;

  input.addEventListener('input', function () {
    clearTimeout(debounceTimer);
    const q = this.value.trim();
    if (q.length < 2) { dropdown.innerHTML = ''; dropdown.style.display = 'none'; return; }

    debounceTimer = setTimeout(function () {
      const ctx = document.body.dataset.ctx || '';
      fetch(ctx + '/properties?action=suggest&q=' + encodeURIComponent(q))
        .then(r => r.json())
        .then(suggestions => {
          dropdown.innerHTML = '';
          if (!suggestions || suggestions.length === 0) {
            dropdown.style.display = 'none';
            return;
          }
          suggestions.forEach(function (s) {
            const item = document.createElement('div');
            item.className = 'suggestion-item';
            item.innerHTML =
              '<i class="bi bi-search me-2"></i>' + s.title +
              '<span class="suggestion-area">' + s.area + '</span>';
            item.addEventListener('click', function () {
              input.value        = s.title;
              dropdown.innerHTML = '';
              dropdown.style.display = 'none';
            });
            dropdown.appendChild(item);
          });
          dropdown.style.display = 'block';
        })
        .catch(() => { dropdown.style.display = 'none'; });
    }, 280);
  });

  document.addEventListener('click', function (e) {
    if (!input.contains(e.target)) {
      dropdown.innerHTML = '';
      dropdown.style.display = 'none';
    }
  });
})();

/* ----------------------------------------------------------------
   5. NOTIFICATION COUNT (AJAX polling - every 60 seconds)
   ---------------------------------------------------------------- */
(function () {
  const bell  = document.getElementById('notifBell');
  const count = document.getElementById('notifCount');
  if (!bell || !count) return;

  function fetchNotifCount() {
    const ctx = document.body.dataset.ctx || '';
    fetch(ctx + '/user/dashboard?action=notif_count')
      .then(r => r.json())
      .then(data => {
        if (data.count > 0) {
          count.textContent    = data.count > 9 ? '9+' : data.count;
          count.style.display  = 'flex';
        } else {
          count.style.display  = 'none';
        }
      })
      .catch(() => {});
  }

  fetchNotifCount();
  setInterval(fetchNotifCount, 60000);
})();

/* ----------------------------------------------------------------
   6. MOBILE SIDEBAR TOGGLE (Dashboard pages)
   ---------------------------------------------------------------- */
(function () {
  const toggle  = document.getElementById('sidebarToggle');
  const sidebar = document.getElementById('dashSidebar');
  const overlay = document.getElementById('sidebarOverlay');
  if (!toggle || !sidebar) return;

  toggle.addEventListener('click', function () {
    sidebar.classList.toggle('open');
    if (overlay) overlay.classList.toggle('show');
  });

  if (overlay) {
    overlay.addEventListener('click', function () {
      sidebar.classList.remove('open');
      overlay.classList.remove('show');
    });
  }
})();

/* ----------------------------------------------------------------
   7. TOAST NOTIFICATION SYSTEM
   ---------------------------------------------------------------- */
function showToast(message, type) {
  type = type || 'info';
  let container = document.getElementById('toastContainer');
  if (!container) {
    container           = document.createElement('div');
    container.id        = 'toastContainer';
    container.style.cssText =
      'position:fixed;bottom:24px;right:24px;z-index:9999;' +
      'display:flex;flex-direction:column;gap:10px;max-width:340px';
    document.body.appendChild(container);
  }

  const icons = {
    success : 'bi-check-circle-fill',
    error   : 'bi-exclamation-circle-fill',
    warning : 'bi-exclamation-triangle-fill',
    info    : 'bi-info-circle-fill'
  };
  const colors = {
    success : '#198754',
    error   : '#dc3545',
    warning : '#fd7e14',
    info    : '#0d6efd'
  };

  const toast = document.createElement('div');
  toast.style.cssText =
    'background:white;border-radius:10px;padding:14px 16px;' +
    'box-shadow:0 4px 20px rgba(0,0,0,0.15);' +
    'display:flex;align-items:center;gap:10px;' +
    'animation:slideInRight 0.3s ease;' +
    'border-left:4px solid ' + (colors[type] || colors.info);
  toast.innerHTML =
    '<i class="bi ' + (icons[type] || icons.info) + '" style="color:' +
    (colors[type] || colors.info) + ';font-size:1.1rem;flex-shrink:0"></i>' +
    '<span style="font-size:0.875rem;font-weight:500;color:#212529">' +
    message + '</span>';

  container.appendChild(toast);
  setTimeout(function () {
    toast.style.opacity   = '0';
    toast.style.transform = 'translateX(20px)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(function () { toast.remove(); }, 300);
  }, 4000);
}

/* Add animation keyframe */
(function () {
  if (document.getElementById('toastStyle')) return;
  const style = document.createElement('style');
  style.id    = 'toastStyle';
  style.textContent =
    '@keyframes slideInRight{from{opacity:0;transform:translateX(20px)}' +
    'to{opacity:1;transform:translateX(0)}}';
  document.head.appendChild(style);
})();

/* ----------------------------------------------------------------
   8. GENERAL UTILITIES
   ---------------------------------------------------------------- */
function formatNaira(amount) {
  return '₦' + parseFloat(amount).toLocaleString('en-NG', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  });
}

function confirmAction(message) {
  return window.confirm(message || 'Are you sure you want to proceed?');
}