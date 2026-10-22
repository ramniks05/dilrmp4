/* DILRMP-MIS 4.0 public pages: main menu toggle and current-page highlight. */
(function () {
    'use strict';

    function init() {
        var nav = document.querySelector('.hp-nav');
        var toggle = document.querySelector('[data-hp-nav-toggle]');
        if (!nav) { return; }

        if (toggle) {
            toggle.addEventListener('click', function () {
                var open = nav.classList.toggle('is-open');
                toggle.setAttribute('aria-expanded', String(open));
            });
            document.addEventListener('keydown', function (event) {
                if (event.key === 'Escape' && nav.classList.contains('is-open')) {
                    nav.classList.remove('is-open');
                    toggle.setAttribute('aria-expanded', 'false');
                }
            });
        }

        var path = window.location.pathname.replace(/\/+$/, '') || '/';
        nav.querySelectorAll('.hp-nav__menu a[href]').forEach(function (link) {
            if (link.target === '_blank') { return; }
            var linkPath = new URL(link.href, window.location.href).pathname.replace(/\/+$/, '') || '/';
            var current = linkPath === path || (linkPath === '/' && path === '/site/home-preview');
            link.classList.toggle('is-active', current);
            if (current) { link.setAttribute('aria-current', 'page'); }
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
