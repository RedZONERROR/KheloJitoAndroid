// ==========================================================================
// KheloJito Android Static Site - Interactive Features
// ==========================================================================

document.addEventListener('DOMContentLoaded', () => {
  // 1. Responsive Mobile Menu Toggle with Backdrop & Accessibility
  const mobileToggle = document.querySelector('.mobile-toggle');
  const navMenu = document.querySelector('.nav-menu');
  const menuBackdrop = document.querySelector('.menu-backdrop');

  const closeMenu = () => {
    if (navMenu && navMenu.classList.contains('open')) {
      navMenu.classList.remove('open');
      if (menuBackdrop) menuBackdrop.classList.remove('open');
      if (mobileToggle) {
        mobileToggle.innerHTML = '☰';
        mobileToggle.setAttribute('aria-expanded', 'false');
      }
      document.body.style.overflow = '';
    }
  };

  const openMenu = () => {
    if (navMenu) {
      navMenu.classList.add('open');
      if (menuBackdrop) menuBackdrop.classList.add('open');
      if (mobileToggle) {
        mobileToggle.innerHTML = '✕';
        mobileToggle.setAttribute('aria-expanded', 'true');
      }
      document.body.style.overflow = 'hidden';
    }
  };

  if (mobileToggle && navMenu) {
    mobileToggle.addEventListener('click', (e) => {
      e.stopPropagation();
      const isOpen = navMenu.classList.contains('open');
      if (isOpen) {
        closeMenu();
      } else {
        openMenu();
      }
    });

    if (menuBackdrop) {
      menuBackdrop.addEventListener('click', closeMenu);
    }

    // Close on any menu link click
    navMenu.querySelectorAll('.nav-link').forEach(link => {
      link.addEventListener('click', closeMenu);
    });

    // Close when clicking outside
    document.addEventListener('click', (e) => {
      if (navMenu.classList.contains('open') && !navMenu.contains(e.target) && !mobileToggle.contains(e.target)) {
        closeMenu();
      }
    });

    // Close on Escape
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') closeMenu();
    });

    // Auto-close on resize to desktop
    window.addEventListener('resize', () => {
      if (window.innerWidth > 768) closeMenu();
    });
  }

  // 2. FAQ Accordion
  const faqItems = document.querySelectorAll('.faq-item');
  faqItems.forEach(item => {
    const questionBtn = item.querySelector('.faq-question');
    if (questionBtn) {
      questionBtn.addEventListener('click', () => {
        const isActive = item.classList.contains('active');
        
        // Close other items
        faqItems.forEach(otherItem => {
          otherItem.classList.remove('active');
        });

        // Toggle clicked
        if (!isActive) {
          item.classList.add('active');
        }
      });
    }
  });

  // 3. Navbar scroll background effect
  const navbar = document.querySelector('.navbar');
  window.addEventListener('scroll', () => {
    if (window.scrollY > 40) {
      navbar.style.background = 'rgba(2, 6, 23, 0.92)';
      navbar.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.5)';
    } else {
      navbar.style.background = 'rgba(2, 6, 23, 0.75)';
      navbar.style.boxShadow = 'none';
    }
  });

  // 4. Animated Number Counters
  const counters = document.querySelectorAll('.metric-number');
  let animated = false;

  const countUp = () => {
    counters.forEach(counter => {
      const target = counter.getAttribute('data-target');
      if (!target) return;

      const num = parseFloat(target.replace(/[^0-9.]/g, ''));
      const prefix = target.startsWith('₹') ? '₹' : '';
      const suffix = target.replace(/[0-9.₹]/g, '');

      let current = 0;
      const step = num / 40;
      const timer = setInterval(() => {
        current += step;
        if (current >= num) {
          counter.textContent = prefix + num + suffix;
          clearInterval(timer);
        } else {
          counter.textContent = prefix + (Number.isInteger(num) ? Math.floor(current) : current.toFixed(1)) + suffix;
        }
      }, 30);
    });
  };

  // 5. Dynamic Web Link Loader (reads link.txt in real time)
  let activeWebUrl = '';

  const applyWebLinks = (url) => {
    if (!url) return;
    const cleanUrl = url.trim();
    if (!cleanUrl) return;
    activeWebUrl = cleanUrl;

    // Update all elements with data-web-link or class web-link
    const webLinks = document.querySelectorAll('[data-web-link], a.web-link');
    webLinks.forEach(link => {
      link.href = cleanUrl;
    });

    // Update og:url meta tag
    const ogMeta = document.getElementById('meta-og-url');
    if (ogMeta) ogMeta.setAttribute('content', cleanUrl);

    // Update any live domain text displays
    const domainDisplays = document.querySelectorAll('[data-web-domain]');
    domainDisplays.forEach(el => {
      el.textContent = cleanUrl.replace(/^https?:\/\//i, '').replace(/\/$/, '');
    });
  };

  // Safe click interceptor in case clicked before fetch promise resolves
  document.addEventListener('click', (e) => {
    const targetLink = e.target.closest('[data-web-link], a.web-link');
    if (targetLink) {
      const currentHref = targetLink.getAttribute('href');
      if (!currentHref || currentHref === '#') {
        e.preventDefault();
        if (activeWebUrl) {
          window.open(activeWebUrl, '_blank', 'noopener,noreferrer');
        } else {
          fetch('link.txt', { cache: 'no-cache' })
            .then(res => res.text())
            .then(text => {
              applyWebLinks(text);
              if (activeWebUrl) {
                window.open(activeWebUrl, '_blank', 'noopener,noreferrer');
              }
            });
        }
      }
    }
  });

  // Fetch link.txt with no-cache so changes are live immediately
  fetch('link.txt', { cache: 'no-cache' })
    .then(response => {
      if (!response.ok) throw new Error('Failed to fetch link.txt: ' + response.status);
      return response.text();
    })
    .then(urlText => {
      applyWebLinks(urlText);
    })
    .catch(err => {
      console.warn('Could not read link.txt:', err);
    });
});
