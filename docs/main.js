// ==========================================================================
// KheloJito Android Static Site - Interactive Features
// ==========================================================================

document.addEventListener('DOMContentLoaded', () => {
  // 1. Mobile Menu Toggle
  const mobileToggle = document.querySelector('.mobile-toggle');
  const navMenu = document.querySelector('.nav-menu');

  if (mobileToggle && navMenu) {
    mobileToggle.addEventListener('click', () => {
      navMenu.classList.toggle('open');
      const isOpen = navMenu.classList.contains('open');
      mobileToggle.innerHTML = isOpen ? '✕' : '☰';
    });

    // Close menu when clicking a link
    navMenu.querySelectorAll('.nav-link').forEach(link => {
      link.addEventListener('click', () => {
        navMenu.classList.remove('open');
        mobileToggle.innerHTML = '☰';
      });
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

  // Trigger when in viewport
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting && !animated) {
        animated = true;
        countUp();
      }
    });
  }, { threshold: 0.5 });

  const metricsSection = document.querySelector('.metrics-section');
  if (metricsSection) {
    observer.observe(metricsSection);
  }
});
