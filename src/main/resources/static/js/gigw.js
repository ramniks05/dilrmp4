/* GIGW 3.0 / WCAG 2.1 accessibility helpers */
(function () {
    'use strict';

    var TEXT_KEY = 'gigwTextSize';
    var CONTRAST_KEY = 'gigwContrast';
    var TEXT_CLASSES = { sm: 'gigw-text-sm', md: '', lg: 'gigw-text-lg', xl: 'gigw-text-xl' };
    var TEXT_STEPS = ['sm', 'md', 'lg', 'xl'];
    var NEW_WINDOW_TEXT = ' (opens in a new window)';
    var EXTERNAL_MESSAGE = 'You are being redirected to an external website. '
        + 'Department of Land Resources is not responsible for the content and privacy policy of external websites.\n\n'
        + 'Do you want to continue?';

    function store(key, value) {
        try { localStorage.setItem(key, value); } catch (e) { /* storage disabled */ }
    }

    function read(key) {
        try { return localStorage.getItem(key); } catch (e) { return null; }
    }

    function applyTextSize(size) {
        if (!TEXT_CLASSES.hasOwnProperty(size)) { size = 'md'; }
        var root = document.documentElement;
        Object.keys(TEXT_CLASSES).forEach(function (k) {
            if (TEXT_CLASSES[k]) { root.classList.remove(TEXT_CLASSES[k]); }
        });
        if (TEXT_CLASSES[size]) { root.classList.add(TEXT_CLASSES[size]); }
        store(TEXT_KEY, size);
        document.querySelectorAll('[data-gigw-text]').forEach(function (btn) {
            var value = btn.getAttribute('data-gigw-text');
            if (value === 'md') { btn.setAttribute('aria-pressed', String(size === 'md')); }
        });
    }

    function stepTextSize(direction) {
        var current = read(TEXT_KEY) || 'md';
        var index = TEXT_STEPS.indexOf(current);
        if (index < 0) { index = 1; }
        index = Math.max(0, Math.min(TEXT_STEPS.length - 1, index + direction));
        applyTextSize(TEXT_STEPS[index]);
    }

    function applyContrast(mode) {
        var high = mode === 'high';
        document.body.classList.toggle('gigw-high-contrast', high);
        store(CONTRAST_KEY, high ? 'high' : 'normal');
        document.querySelectorAll('[data-gigw-contrast]').forEach(function (btn) {
            btn.setAttribute('aria-pressed', String(btn.getAttribute('data-gigw-contrast') === (high ? 'high' : 'normal')));
        });
    }

    function isGovernmentHost(host) {
        host = (host || '').toLowerCase();
        return host === 'gov.in' || host === 'nic.in'
            || /\.gov\.in$/.test(host) || /\.nic\.in$/.test(host);
    }

    function isExternal(link) {
        return link.host && link.host !== window.location.host
            && (link.protocol === 'http:' || link.protocol === 'https:');
    }

    function markLinks(root) {
        (root || document).querySelectorAll('a[target="_blank"]').forEach(function (link) {
            if (link.getAttribute('data-gigw-marked')) { return; }
            link.setAttribute('data-gigw-marked', '1');
            var rel = (link.getAttribute('rel') || '').split(/\s+/);
            if (rel.indexOf('noopener') < 0) { rel.push('noopener'); }
            if (rel.indexOf('noreferrer') < 0) { rel.push('noreferrer'); }
            link.setAttribute('rel', rel.join(' ').trim());
            if (!link.getAttribute('title')) {
                var label = (link.textContent || '').trim();
                if (!label) {
                    var img = link.querySelector('img[alt]');
                    label = img ? img.getAttribute('alt') : '';
                }
                link.setAttribute('title', (label ? label + ' - ' : '') + 'opens in a new window');
            }
            var hint = document.createElement('span');
            hint.className = 'sr-only-gigw';
            hint.textContent = NEW_WINDOW_TEXT;
            link.appendChild(hint);
        });
    }

    function initTicker(root) {
        (root || document).querySelectorAll('[data-gigw-ticker-toggle]').forEach(function (btn) {
            if (btn.getAttribute('data-gigw-ready')) { return; }
            btn.setAttribute('data-gigw-ready', '1');
            btn.addEventListener('click', function () {
                var ticker = btn.closest('.gigw-ticker');
                if (!ticker) { return; }
                var paused = ticker.classList.toggle('is-paused');
                btn.setAttribute('aria-pressed', String(paused));
                btn.textContent = paused ? 'Play' : 'Pause';
                btn.setAttribute('aria-label', paused ? 'Play scrolling announcement' : 'Pause scrolling announcement');
            });
        });
    }

    document.addEventListener('click', function (event) {
        var control = event.target.closest('[data-gigw-text], [data-gigw-contrast]');
        if (control) {
            event.preventDefault();
            var text = control.getAttribute('data-gigw-text');
            if (text === 'dec') { stepTextSize(-1); }
            else if (text === 'inc') { stepTextSize(1); }
            else if (text) { applyTextSize(text); }
            var contrast = control.getAttribute('data-gigw-contrast');
            if (contrast) { applyContrast(contrast); }
            return;
        }

        var link = event.target.closest('a[href]');
        if (link && isExternal(link) && !isGovernmentHost(link.hostname)
            && !link.hasAttribute('data-gigw-no-confirm')) {
            if (!window.confirm(EXTERNAL_MESSAGE)) {
                event.preventDefault();
            }
        }
    });

    function init() {
        applyTextSize(read(TEXT_KEY) || 'md');
        applyContrast(read(CONTRAST_KEY) || 'normal');
        markLinks(document);
        initTicker(document);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    document.addEventListener('htmx:afterSwap', function (event) {
        markLinks(event.target);
        initTicker(event.target);
    });
})();
