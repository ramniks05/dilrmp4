/* DILRMP-MIS 4.0 home page: national insight blocks drawn from /site/home-preview/insights. */
(function () {
    'use strict';

    var TREND_COLOURS = ['#012c6f', '#fa8c16', '#08979c', '#128937', '#7a5af8', '#d4380d', '#c41d7f'];
    var MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    function esc(text) {
        return String(text == null ? '' : text).replace(/[&<>"']/g, function (ch) {
            return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[ch];
        });
    }

    function num(v) {
        var n = Number(v);
        return isFinite(n) ? n : 0;
    }

    function indian(v) {
        return Math.round(num(v)).toLocaleString('en-IN');
    }

    function compact(v) {
        var n = num(v);
        if (n >= 1e7) { return (n / 1e7).toFixed(2).replace(/\.?0+$/, '') + ' crore'; }
        if (n >= 1e5) { return (n / 1e5).toFixed(2).replace(/\.?0+$/, '') + ' lakh'; }
        return indian(n);
    }

    function pct(part, whole) {
        return num(whole) > 0 ? Math.min(100, num(part) / num(whole) * 100) : 0;
    }

    function titleCase(name) {
        return String(name || '').toLowerCase().replace(/(^|[\s(&-])([a-z])/g, function (m, p, c) { return p + c.toUpperCase(); })
            .replace(/\bAnd\b/g, 'and');
    }

    function host(id) {
        return document.getElementById(id);
    }

    function unavailable(id) {
        var el = host(id);
        if (el) { el.innerHTML = '<p class="hp-note">Data not available right now.</p>'; }
    }

    function monthLabel(ym) {
        var parts = String(ym).split('-');
        return MONTHS[num(parts[1]) - 1] + ' ' + String(parts[0]).slice(2);
    }

    function renderChain(chain) {
        var el = host('hp-chain');
        if (!chain || !chain.steps) { unavailable('hp-chain'); return; }
        el.innerHTML = chain.steps.map(function (s) {
            return '<li class="hp-chain__step" style="--p:' + s.percent + '%">'
                + '<span class="hp-chain__label">' + esc(s.label) + '</span>'
                + '<b class="hp-chain__pct">' + num(s.percent).toFixed(1) + '%</b>'
                + '<span class="hp-chain__bar"><i></i></span>'
                + '<small>' + compact(s.value) + ' of ' + compact(s.total) + ' ' + esc(s.unit) + '</small>'
                + '</li>';
        }).join('');
    }

    function renderServices(services) {
        if (!services || !services.items) { unavailable('hp-services'); return; }
        var total = services.totalStates || 36;
        var groups = {};
        var order = [];
        services.items.forEach(function (item, index) {
            if (!groups[item.group]) { groups[item.group] = []; order.push(item.group); }
            groups[item.group].push({ item: item, index: index });
        });
        host('hp-services').innerHTML = order.map(function (g) {
            return '<div class="hp-svc-group"><h3>' + esc(g) + '</h3><ul>'
                + groups[g].map(function (e) {
                    var p = pct(e.item.count, total);
                    return '<li><span class="hp-svc__label">' + esc(e.item.label) + '</span>'
                        + '<span class="hp-svc__count"><b>' + e.item.count + '</b> / ' + total + '</span>'
                        + '<span class="hp-svc__bar"><i style="width:' + p.toFixed(1) + '%"></i></span></li>';
                }).join('')
                + '</ul></div>';
        }).join('');

        var head = '<tr><th scope="col">State/UT</th>' + services.items.map(function (item) {
            return '<th scope="col"><span>' + esc(item.label) + '</span></th>';
        }).join('') + '</tr>';
        var rows = services.states.map(function (s) {
            return '<tr><th scope="row">' + esc(titleCase(s.name)) + '</th>' + s.flags.map(function (f, i) {
                return '<td><span class="hp-dot ' + (f ? 'is-yes' : 'is-no') + '" title="' + esc(services.items[i].label) + ': ' + (f ? 'Yes' : 'No') + '">'
                    + (f ? 'Yes' : 'No') + '</span></td>';
            }).join('') + '</tr>';
        }).join('');
        host('hp-matrix').innerHTML = '<table><thead>' + head + '</thead><tbody>' + rows + '</tbody></table>';
    }

    function renderSurvey(survey) {
        if (!survey || !survey.stages) { unavailable('hp-funnel'); unavailable('hp-area'); return; }
        var top = Math.max.apply(null, survey.stages.map(function (s) { return num(s.villages); }).concat([1]));
        host('hp-funnel').innerHTML = survey.stages.map(function (s, i) {
            var w = pct(s.villages, top);
            var conv = i === 0 ? '' : '<em>' + pct(s.villages, survey.stages[i - 1].villages).toFixed(0) + '% of previous stage</em>';
            return '<div class="hp-funnel__row"><span class="hp-funnel__label">' + esc(s.label) + conv + '</span>'
                + '<span class="hp-funnel__bar" style="--w:' + w.toFixed(1) + '%"><b>' + indian(s.villages) + '</b></span></div>';
        }).join('');
        host('hp-funnel-note').textContent = indian(survey.notStarted) + ' sanctioned villages have not started survey yet. Total villages: '
            + indian(survey.totalVillages) + '.';

        var total = num(survey.totalArea);
        var rows = [
            { label: 'Total rural revenue area', value: total, cls: 'is-total' },
            { label: 'Sanctioned for survey', value: num(survey.areaSanctioned), cls: 'is-sanctioned' },
            { label: 'Drone flying completed', value: num(survey.areaFlown), cls: 'is-flown' }
        ];
        host('hp-area').innerHTML = rows.map(function (r) {
            return '<div class="hp-area__row ' + r.cls + '"><div class="hp-area__top"><span>' + esc(r.label) + '</span><b>' + indian(r.value) + '</b></div>'
                + '<span class="hp-area__bar"><i style="width:' + pct(r.value, total).toFixed(1) + '%"></i></span>'
                + '<small>' + pct(r.value, total).toFixed(1) + '% of rural area</small></div>';
        }).join('')
            + '<p class="hp-note">' + pct(survey.areaFlown, survey.areaSanctioned).toFixed(1) + '% of the sanctioned area has been flown.</p>';
    }

    function donut(el, share, colour) {
        var r = 40;
        var c = 2 * Math.PI * r;
        el.innerHTML = '<svg viewBox="0 0 100 100"><circle cx="50" cy="50" r="' + r + '" fill="none" stroke="#e7f2ff" stroke-width="14"/>'
            + '<circle cx="50" cy="50" r="' + r + '" fill="none" stroke="' + colour + '" stroke-width="14" stroke-linecap="round"'
            + ' stroke-dasharray="' + (share / 100 * c).toFixed(2) + ' ' + c.toFixed(2) + '" transform="rotate(-90 50 50)"/></svg>';
    }

    function renderWomen(women) {
        if (!women) { unavailable('hp-women'); return; }
        var share = pct(women.female, num(women.female) + num(women.male));
        donut(host('hp-women-donut'), share, '#c41d7f');
        var shareEl = host('hp-women-share');
        shareEl.removeAttribute('data-skeleton');
        shareEl.textContent = share.toFixed(1) + '%';
        host('hp-women-counts').textContent = compact(women.female) + ' women and ' + compact(women.male) + ' men owners recorded';
        host('hp-women-top').innerHTML = (women.top || []).map(function (s) {
            return '<li><span>' + esc(titleCase(s.name)) + '</span><span class="hp-toplist__bar"><i style="width:' + num(s.share).toFixed(1) + '%"></i></span><b>'
                + num(s.share).toFixed(1) + '%</b></li>';
        }).join('');
        host('hp-women-note').textContent = 'Based on ' + women.reportingStates + ' States/UTs that report owners by gender.';
    }

    function splitBar(label, parts, total, unit, extra) {
        var segs = parts.map(function (p) {
            return '<i class="' + p.cls + '" style="width:' + pct(p.value, total).toFixed(2) + '%" title="' + esc(p.label) + ': ' + indian(p.value) + '"></i>';
        }).join('');
        var legend = parts.map(function (p) {
            return '<span class="' + p.cls + '">' + esc(p.label) + ' <b>' + compact(p.value) + '</b></span>';
        }).join('');
        var done = parts.reduce(function (a, p) { return a + num(p.value); }, 0);
        return '<div class="hp-modern__item"><div class="hp-modern__top"><b>' + esc(label) + '</b><span>' + pct(done, total).toFixed(1) + '% of '
            + compact(total) + ' ' + esc(unit) + '</span></div>'
            + '<span class="hp-split-bar">' + segs + '</span><div class="hp-split-legend">' + legend + '</div>'
            + (extra ? '<small>' + extra + '</small>' : '') + '</div>';
    }

    function yearNote(m) {
        if (!m || !m.yearEarliest) { return ''; }
        return 'Digitized up to the year ' + esc(m.yearEarliest) + ' to ' + esc(m.yearLatest) + ', as reported by ' + indian(m.yearDistricts) + ' districts.';
    }

    function renderModernisation(mod) {
        if (!mod) { unavailable('hp-modern'); return; }
        var html = '';
        if (mod.sro) {
            html += splitBar('SRO modernization', [
                { label: 'State / PPP funds', value: mod.sro.stateFunds, cls: 'is-state' },
                { label: 'DILRMP funds', value: mod.sro.dilrmpFunds, cls: 'is-dilrmp' }
            ], mod.sro.online, 'SROs with online registration');
        }
        if (mod.registered) {
            html += splitBar('Legacy registered documents', [
                { label: 'State funds', value: mod.registered.stateFunds, cls: 'is-state' },
                { label: 'DILRMP funds', value: mod.registered.dilrmpFunds, cls: 'is-dilrmp' }
            ], mod.registered.total, 'pages', yearNote(mod.registered));
        }
        if (mod.revenue) {
            html += splitBar('Legacy revenue records', [
                { label: 'State funds', value: mod.revenue.stateFunds, cls: 'is-state' },
                { label: 'DILRMP funds', value: mod.revenue.dilrmpFunds, cls: 'is-dilrmp' }
            ], mod.revenue.total, 'pages', yearNote(mod.revenue));
        }
        host('hp-modern').innerHTML = html || '<p class="hp-note">Data not available right now.</p>';
    }

    function renderLeaders(leaders) {
        if (!leaders || !leaders.length) { unavailable('hp-leaders'); return; }
        host('hp-leaders').innerHTML = leaders.map(function (l) {
            var names = l.complete || [];
            var shown = names.slice(0, 6).map(function (n) { return '<span class="hp-chip-s">' + esc(titleCase(n)) + '</span>'; }).join('');
            var more = names.length > 6 ? '<span class="hp-chip-s is-more" title="' + esc(names.slice(6).map(titleCase).join(', ')) + '">+' + (names.length - 6) + ' more</span>' : '';
            return '<article class="hp-leader">'
                + '<div class="hp-leader__head"><span class="hp-leader__count">' + names.length + '</span>'
                + '<span><b>' + esc(l.label) + '</b><small>States/UTs complete &middot; ' + l.above90 + ' at 90%+ &middot; India '
                + (l.national == null ? '-' : num(l.national).toFixed(1) + '%') + '</small></span></div>'
                + '<div class="hp-leader__chips">' + (shown + more || '<span class="hp-note">None yet</span>') + '</div>'
                + '</article>';
        }).join('');
    }

    function renderTrend(trend) {
        if (!trend || !trend.months || !trend.months.length) { unavailable('hp-trend'); return; }
        var hidden = {};
        var W = 960, H = 320, L = 44, R = 16, T = 16, B = 34;
        var n = trend.months.length;
        function x(i) { return L + (n === 1 ? 0 : i * (W - L - R) / (n - 1)); }
        function y(v) { return T + (100 - v) / 100 * (H - T - B); }

        function draw() {
            var grid = [0, 25, 50, 75, 100].map(function (v) {
                return '<line x1="' + L + '" x2="' + (W - R) + '" y1="' + y(v) + '" y2="' + y(v) + '" class="hp-trend__grid"/>'
                    + '<text x="' + (L - 8) + '" y="' + (y(v) + 4) + '" text-anchor="end" class="hp-trend__axis">' + v + '%</text>';
            }).join('');
            var step = Math.max(1, Math.ceil(n / 9));
            var labels = trend.months.map(function (m, i) {
                return i % step === 0 || i === n - 1
                    ? '<text x="' + x(i) + '" y="' + (H - 10) + '" text-anchor="middle" class="hp-trend__axis">' + monthLabel(m) + '</text>' : '';
            }).join('');
            var lines = trend.series.map(function (s, si) {
                if (hidden[si]) { return ''; }
                var colour = TREND_COLOURS[si % TREND_COLOURS.length];
                var d = '';
                var last = null;
                s.values.forEach(function (v, i) {
                    if (v == null) { return; }
                    d += (d ? ' L' : 'M') + x(i).toFixed(1) + ' ' + y(num(v)).toFixed(1);
                    last = { i: i, v: num(v) };
                });
                if (!d) { return ''; }
                return '<path d="' + d + '" fill="none" stroke="' + colour + '" stroke-width="2.5" stroke-linejoin="round" stroke-linecap="round"/>'
                    + '<circle cx="' + x(last.i) + '" cy="' + y(last.v) + '" r="4" fill="' + colour + '"><title>' + esc(s.label) + ': ' + last.v.toFixed(1) + '%</title></circle>';
            }).join('');
            host('hp-trend').innerHTML = '<svg viewBox="0 0 ' + W + ' ' + H + '" role="img" aria-label="National progress by month">' + grid + labels + lines + '</svg>';
        }

        host('hp-trend-legend').innerHTML = trend.series.map(function (s, si) {
            var vals = s.values.filter(function (v) { return v != null; });
            var first = vals.length ? num(vals[0]) : 0;
            var last = vals.length ? num(vals[vals.length - 1]) : 0;
            var delta = last - first;
            return '<button type="button" aria-pressed="true" data-series="' + si + '" style="--c:' + TREND_COLOURS[si % TREND_COLOURS.length] + '">'
                + '<i></i>' + esc(s.label) + ' <b>' + last.toFixed(1) + '%</b><small' + (delta < 0 ? ' class="is-down"' : '') + '>'
                + (delta >= 0 ? '+' : '') + delta.toFixed(1) + ' pts</small></button>';
        }).join('');
        host('hp-trend-legend').querySelectorAll('button').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var si = btn.getAttribute('data-series');
                hidden[si] = !hidden[si];
                btn.setAttribute('aria-pressed', String(!hidden[si]));
                draw();
            });
        });
        draw();
    }

    function renderFreshness(f) {
        if (!f) { unavailable('hp-fresh'); return; }
        var cover = pct(f.reportedDistricts, f.totalDistricts);
        var gaps = (f.states || []).filter(function (s) { return s.total > 0 && s.reported < s.total; })
            .sort(function (a, b) { return pct(a.reported, a.total) - pct(b.reported, b.total); }).slice(0, 5);
        var stale = (f.states || []).filter(function (s) { return s.daysAgo != null; })
            .sort(function (a, b) { return b.daysAgo - a.daysAgo; }).slice(0, 5);
        var wrap = document.createElement('div');
        donut(wrap, cover, '#128937');
        host('hp-fresh').innerHTML = '<div class="hp-women"><div class="hp-donut">' + wrap.innerHTML + '</div>'
            + '<div class="hp-women__text"><b class="hp-women__share">' + cover.toFixed(1) + '%</b>'
            + '<span>' + indian(f.reportedDistricts) + ' of ' + indian(f.totalDistricts) + ' districts have submitted District MIS</span>'
            + '<small>' + indian(f.updated30) + ' updated in the last 30 days &middot; ' + indian(f.updated90) + ' in the last 90 days</small></div></div>'
            + '<div class="hp-fresh__lists">'
            + '<div><h4 class="hp-mini-title">Districts yet to report</h4><ul class="hp-plain">'
            + (gaps.length ? gaps.map(function (s) { return '<li><span>' + esc(titleCase(s.name)) + '</span><b>' + (s.total - s.reported) + ' of ' + s.total + '</b></li>'; }).join('')
                : '<li><span>All districts have reported</span></li>')
            + '</ul></div>'
            + '<div><h4 class="hp-mini-title">Longest since last update</h4><ul class="hp-plain">'
            + stale.map(function (s) { return '<li><span>' + esc(titleCase(s.name)) + '</span><b>' + s.daysAgo + ' days</b></li>'; }).join('')
            + '</ul></div></div>';
    }

    function renderSafeguards(s, totalStates) {
        if (!s) { unavailable('hp-safe'); return; }
        var links = (s.linkages || []).map(function (l) {
            return '<div class="hp-area__row"><div class="hp-area__top"><span>' + esc(l.label) + '</span><b>' + num(l.percent).toFixed(1) + '%</b></div>'
                + '<span class="hp-area__bar"><i style="width:' + num(l.percent).toFixed(1) + '%"></i></span>'
                + '<small>' + compact(l.value) + ' of ' + compact(l.total) + ' ' + esc(l.unit) + '</small></div>';
        }).join('');
        var flags = (s.stateFlags || []).map(function (f) {
            return '<li><span>' + esc(f.label) + '</span><b>' + f.states + ' / ' + totalStates + '</b></li>';
        }).join('');
        host('hp-safe').innerHTML = links
            + '<div class="hp-safe__mortgage"><b>' + indian(s.mortgageDistricts) + '</b><span>districts and <b>' + indian(s.mortgageBranches)
            + '</b> bank branches red-flag mortgages in land records</span></div>'
            + '<h4 class="hp-mini-title">States/UTs with the safeguard</h4><ul class="hp-plain">' + flags + '</ul>';
    }

    var MAP_TO_LEADER = {
        clr: 'clr', MapDigitization: 'maps', sro: 'sro', mrr: 'mrr',
        surveyReservey: 'survey', rccms: 'rcms', linkedwithaadhaar: 'aadhaar',
        sroModernization: 'sroModernization', legacyRegistered: 'legacyRegistered', legacyRevenue: 'legacyRevenue'
    };
    var leadersByKey = {};
    var focusId = 'clr';

    function renderFocus() {
        var leader = leadersByKey[MAP_TO_LEADER[focusId]];
        if (!leader) { return; }
        var value = leader.national == null ? 0 : num(leader.national);
        host('hp-focus-label').textContent = leader.label;
        var valueEl = host('hp-focus-value');
        valueEl.removeAttribute('data-skeleton');
        valueEl.textContent = value.toFixed(1) + '%';
        host('hp-focus-bar').style.width = Math.min(100, value).toFixed(1) + '%';
        host('hp-focus-note').textContent = (leader.complete || []).length + ' States/UTs complete \u00b7 ' + leader.above90 + ' at 90% or more';
    }

    document.addEventListener('hp:component', function (event) {
        focusId = event.detail;
        renderFocus();
    });

    function loadInsights() {
        $.ajax({ url: '/site/home-preview/insights', method: 'GET' })
            .done(function (data) {
                (data.leaders || []).forEach(function (l) { leadersByKey[l.key] = l; });
                renderFocus();
                document.dispatchEvent(new CustomEvent('hp:insights', { detail: data }));
                renderChain(data.chain);
                renderServices(data.services);
                renderSurvey(data.survey);
                renderWomen(data.women);
                renderModernisation(data.modernisation);
                renderLeaders(data.leaders);
                renderTrend(data.trend);
                renderFreshness(data.freshness);
                renderSafeguards(data.safeguards, data.services ? data.services.totalStates : 36);
            })
            .fail(function () {
                ['hp-chain', 'hp-services', 'hp-funnel', 'hp-area', 'hp-modern', 'hp-leaders', 'hp-trend', 'hp-fresh', 'hp-safe'].forEach(unavailable);
            });
    }

    $(loadInsights);
})();
