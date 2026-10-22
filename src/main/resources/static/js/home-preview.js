/* DILRMP-MIS 4.0 home page design preview. Map and State/UT table are drawn by the existing statemap.js. */
(function () {
    'use strict';

    var RING_LENGTH = 113.1;
    var reduceMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    function num(value) {
        var n = Number(value);
        return isFinite(n) ? n : 0;
    }

    function formatIndian(value) {
        return Math.round(num(value)).toLocaleString('en-IN');
    }

    function clearSkeleton(el) {
        el.removeAttribute('data-skeleton');
    }

    function animateCount(el, target) {
        clearSkeleton(el);
        if (reduceMotion || target <= 0) {
            el.textContent = formatIndian(target);
            return;
        }
        var start = null;
        var duration = 1100;
        function step(ts) {
            if (start === null) { start = ts; }
            var t = Math.min(1, (ts - start) / duration);
            var eased = 1 - Math.pow(1 - t, 3);
            el.textContent = formatIndian(target * eased);
            if (t < 1) { window.requestAnimationFrame(step); }
        }
        window.requestAnimationFrame(step);
    }

    function setRing(ring, percent) {
        var p = Math.max(0, Math.min(100, num(percent)));
        ring.querySelector('.hp-ring__bar').style.strokeDasharray = (p / 100 * RING_LENGTH).toFixed(2) + ' ' + RING_LENGTH;
        ring.querySelector('b').textContent = p.toFixed(1) + '%';
        ring.setAttribute('title', p.toFixed(2) + '%');
    }

    function renderNational(national) {
        document.querySelectorAll('[data-key]').forEach(function (el) {
            animateCount(el, num(national[el.getAttribute('data-key')]));
        });
        document.querySelectorAll('[data-ring]').forEach(function (ring) {
            setRing(ring, national[ring.getAttribute('data-ring')]);
        });

        animateCount(document.getElementById('hp-totalDistrict'), num(national.totalDistrict));
        animateCount(document.getElementById('hp-totalTehsil'), num(national.totalTehsil));
        animateCount(document.getElementById('hp-totalVillage'), num(national.totalVillage));
        clearSkeleton(document.getElementById('hp-totalState'));

        var villages = num(national.totalVillage);
        document.querySelectorAll('[data-ring-village]').forEach(function (ring) {
            var value = num(national[ring.getAttribute('data-ring-village')]);
            setRing(ring, villages > 0 ? value / villages * 100 : 0);
        });
    }

    function renderComponentTotals(totals) {
        document.querySelectorAll('[data-key-x]').forEach(function (el) {
            animateCount(el, num(totals[el.getAttribute('data-key-x')]));
        });
        document.querySelectorAll('[data-ring-x]').forEach(function (ring) {
            setRing(ring, totals[ring.getAttribute('data-ring-x')]);
        });
    }

    function markUnavailable(selector) {
        document.querySelectorAll(selector).forEach(function (el) {
            if (!el.hasAttribute('data-skeleton')) { return; }
            clearSkeleton(el);
            el.textContent = '-';
        });
    }

    function loadDashboard() {
        $.ajax({ url: '/getDashboardData', method: 'GET' })
            .done(function (data) { renderNational(data.nationalDashBordDTOList || {}); })
            .fail(function () { markUnavailable('[data-key][data-skeleton], .hp-count[data-skeleton]'); });

        $.ajax({ url: '/site/home-preview/component-totals', method: 'GET' })
            .done(renderComponentTotals)
            .fail(function () { markUnavailable('[data-key-x]'); });
    }

    function setActiveChip(id) {
        document.querySelectorAll('[data-hp-select]').forEach(function (chip) {
            var on = chip.getAttribute('data-hp-select') === id;
            chip.classList.toggle('is-active', on);
            chip.setAttribute('aria-pressed', String(on));
        });
    }

    /* Components drawn from District MIS by hpGenericStateMap rather than the /api/chart blocks in statemap.js. */
    var MIS_MAP_COMPONENTS = { sroModernization: true, legacyRegistered: true, legacyRevenue: true };

    function markActiveCard(id) {
        document.querySelectorAll('.small-box').forEach(function (box) {
            box.classList.remove('active-box');
            box.style.backgroundColor = '';
        });
        var anchor = document.getElementById(id);
        var box = anchor && anchor.closest('.small-box');
        if (box) { box.classList.add('active-box'); }
    }

    function selectComponent(id, scroll) {
        if (MIS_MAP_COMPONENTS[id]) {
            if (typeof window.hpLoadGenericMap !== 'function') { return; }
            window.hpLoadGenericMap(id);
            markActiveCard(id);
        } else {
            if (typeof window.loadMatribhoomiData !== 'function') { return; }
            window.loadMatribhoomiData(id);
        }
        setActiveChip(id);
        document.dispatchEvent(new CustomEvent('hp:component', { detail: id }));
        if (scroll) {
            document.getElementById('map').scrollIntoView({ behavior: reduceMotion ? 'auto' : 'smooth', block: 'start' });
        }
    }

    function initComponents() {
        document.querySelectorAll('[data-hp-component]').forEach(function (link) {
            link.addEventListener('click', function (event) {
                event.preventDefault();
                selectComponent(link.id, true);
            });
        });
        document.querySelectorAll('[data-hp-select]').forEach(function (chip) {
            chip.addEventListener('click', function () {
                selectComponent(chip.getAttribute('data-hp-select'), false);
            });
        });
        var first = document.getElementById('clr');
        if (first) { first.closest('.small-box').classList.add('active-box'); }
        setActiveChip('clr');
    }

    function initNav() {
        var nav = document.querySelector('.hp-nav');
        var toggle = document.querySelector('[data-hp-nav-toggle]');
        if (toggle) {
            toggle.addEventListener('click', function () {
                var open = nav.classList.toggle('is-open');
                toggle.setAttribute('aria-expanded', String(open));
            });
        }

        document.querySelectorAll('[data-hp-sub]').forEach(function (btn) {
            btn.addEventListener('click', function (event) {
                event.stopPropagation();
                var item = btn.parentElement;
                var open = !item.classList.contains('is-open');
                document.querySelectorAll('.hp-has-sub.is-open').forEach(function (other) {
                    other.classList.remove('is-open');
                    other.querySelector('[data-hp-sub]').setAttribute('aria-expanded', 'false');
                });
                item.classList.toggle('is-open', open);
                btn.setAttribute('aria-expanded', String(open));
            });
        });

        document.addEventListener('click', function () {
            document.querySelectorAll('.hp-has-sub.is-open').forEach(function (item) {
                item.classList.remove('is-open');
                item.querySelector('[data-hp-sub]').setAttribute('aria-expanded', 'false');
            });
        });

        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape') {
                document.querySelectorAll('.hp-has-sub.is-open').forEach(function (item) { item.classList.remove('is-open'); });
                if (nav.classList.contains('is-open')) {
                    nav.classList.remove('is-open');
                    toggle.setAttribute('aria-expanded', 'false');
                }
            }
        });

        var links = Array.prototype.slice.call(document.querySelectorAll('.hp-nav__menu > li > a[href^="#"]'));
        if (!('IntersectionObserver' in window) || !links.length) { return; }
        var observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (!entry.isIntersecting) { return; }
                links.forEach(function (link) {
                    var on = link.getAttribute('href') === '#' + entry.target.id;
                    link.classList.toggle('is-active', on);
                    if (on) { link.setAttribute('aria-current', 'page'); } else { link.removeAttribute('aria-current'); }
                });
            });
        }, { rootMargin: '-45% 0px -50% 0px' });
        links.forEach(function (link) {
            var section = document.querySelector(link.getAttribute('href'));
            if (section) { observer.observe(section); }
        });
        links.forEach(function (link) {
            link.addEventListener('click', function () {
                nav.classList.remove('is-open');
                if (toggle) { toggle.setAttribute('aria-expanded', 'false'); }
            });
        });
    }

    function initAsOn() {
        var el = document.getElementById('hp-asOn');
        if (el) {
            el.textContent = 'as on ' + new Date().toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
        }
    }

    $(function () {
        initAsOn();
        initNav();
        initComponents();
        loadDashboard();
    });
})();
