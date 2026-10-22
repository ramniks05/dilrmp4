/* DILRMP-MIS 4.0 signed-in workspace: current menu item, page title in the top bar and the clock. */
(function () {
    'use strict';

    function linkLabel(link) {
        var p = link.querySelector('p');
        return ((p ? p.firstChild && p.firstChild.textContent : link.textContent) || '').trim();
    }

    function markActive(link) {
        var menu = document.querySelector('.mis-sidebar .nav-sidebar');
        if (!menu || !link) { return; }
        menu.querySelectorAll('.nav-link.active, .nav-link.is-trail').forEach(function (el) {
            el.classList.remove('active', 'is-trail');
        });
        link.classList.add('active');

        var item = link.closest('.nav-treeview');
        while (item) {
            var parent = item.closest('.nav-item');
            if (!parent) { break; }
            parent.classList.add('menu-open');
            var parentLink = parent.querySelector(':scope > .nav-link');
            if (parentLink) { parentLink.classList.add('is-trail'); }
            item = parent.parentElement ? parent.parentElement.closest('.nav-treeview') : null;
        }

        var title = document.getElementById('misPageTitle');
        var label = linkLabel(link);
        if (title && label) { title.textContent = label; }
    }

    function initMenu() {
        var menu = document.querySelector('.mis-sidebar .nav-sidebar');
        if (!menu) { return; }

        var path = window.location.pathname.replace(/\/+$/, '') || '/';
        menu.querySelectorAll('a.nav-link[href]').forEach(function (link) {
            var href = link.getAttribute('href');
            if (!href || href === '#' || link.hasAttribute('hx-get')) { return; }
            var linkPath = new URL(link.href, window.location.href).pathname.replace(/\/+$/, '') || '/';
            if (linkPath === path && !link.classList.contains('mis-nav-logout')) { markActive(link); }
        });

        menu.addEventListener('click', function (event) {
            var link = event.target.closest('a.nav-link');
            if (!link || !link.hasAttribute('hx-get')) { return; }
            markActive(link);
            if (window.innerWidth < 992) {
                document.body.classList.remove('sidebar-open');
                document.body.classList.add('sidebar-collapse');
            }
        });
    }

    function initClock() {
        var clock = document.getElementById('currentTime');
        if (!clock) { return; }
        var format = { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' };
        // Some pages (Naksha) run their own clock in this element; only start ours if nobody else has.
        window.setTimeout(function () {
            if (clock.textContent.trim()) { return; }
            var tick = function () { clock.textContent = new Date().toLocaleString('en-IN', format); };
            tick();
            window.setInterval(tick, 30000);
        }, 1500);
    }

    function init() {
        initMenu();
        initClock();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
