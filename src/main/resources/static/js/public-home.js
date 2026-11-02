(function () {
    var root = document.querySelector("[data-ph-slider]");
    if (!root) {
        return;
    }

    var slides = Array.prototype.slice.call(root.querySelectorAll(".ph-slider__slide"));
    var dots = Array.prototype.slice.call(root.querySelectorAll("[data-ph-go]"));
    var pauseBtn = root.querySelector("[data-ph-pause]");
    var index = 0;
    var timer = null;
    var paused = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    function show(next) {
        index = (next + slides.length) % slides.length;
        slides.forEach(function (slide, i) {
            var on = i === index;
            slide.classList.toggle("is-active", on);
            if (on) {
                slide.removeAttribute("hidden");
            } else {
                slide.setAttribute("hidden", "");
            }
        });
        dots.forEach(function (dot, i) {
            if (i === index) {
                dot.setAttribute("aria-current", "true");
            } else {
                dot.removeAttribute("aria-current");
            }
        });
    }

    function play() {
        clearInterval(timer);
        if (paused) {
            return;
        }
        timer = setInterval(function () {
            show(index + 1);
        }, 7000);
    }

    function setPaused(value) {
        paused = value;
        if (pauseBtn) {
            pauseBtn.textContent = paused ? "Play" : "Pause";
            pauseBtn.setAttribute("aria-label", paused ? "Play" : "Pause");
        }
        play();
    }

    root.querySelector("[data-ph-prev]").addEventListener("click", function () {
        show(index - 1);
        play();
    });
    root.querySelector("[data-ph-next]").addEventListener("click", function () {
        show(index + 1);
        play();
    });
    dots.forEach(function (dot) {
        dot.addEventListener("click", function () {
            show(Number(dot.getAttribute("data-ph-go")));
            play();
        });
    });
    pauseBtn.addEventListener("click", function () {
        setPaused(!paused);
    });
    root.addEventListener("mouseenter", function () {
        clearInterval(timer);
    });
    root.addEventListener("mouseleave", play);
    root.addEventListener("focusin", function () {
        clearInterval(timer);
    });
    root.addEventListener("focusout", function (event) {
        if (!root.contains(event.relatedTarget)) {
            play();
        }
    });

    show(0);
    setPaused(paused);
})();

(function () {
    var ticker = document.querySelector(".ph-ticker");
    var button = ticker && ticker.querySelector("[data-ph-ticker-pause]");
    if (!button) {
        return;
    }
    var reduce = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    function apply(paused) {
        ticker.classList.toggle("is-paused", paused);
        button.textContent = paused ? "Play" : "Pause";
        button.setAttribute("aria-label", paused ? "Play announcements" : "Pause announcements");
    }
    apply(reduce);
    button.addEventListener("click", function () {
        apply(!ticker.classList.contains("is-paused"));
    });
})();

(function () {
    var root = document.querySelector("[data-ph-metrics]");
    if (!root) {
        return;
    }

    function formatCount(value) {
        var n = Number(value);
        if (!isFinite(n)) {
            return "—";
        }
        return Math.round(n).toLocaleString("en-IN");
    }

    function formatPercent(value) {
        var n = Number(value);
        if (!isFinite(n)) {
            return "—";
        }
        return n.toFixed(1) + "%";
    }

    fetch("/getDashboardData", { headers: { Accept: "application/json" } })
        .then(function (response) {
            if (!response.ok) {
                throw new Error("dashboard");
            }
            return response.json();
        })
        .then(function (data) {
            var national = (data && data.nationalDashBordDTOList) || {};
            root.querySelectorAll("[data-ph-count]").forEach(function (el) {
                el.textContent = formatCount(national[el.getAttribute("data-ph-count")]);
            });
            root.querySelectorAll("[data-ph-percent]").forEach(function (el) {
                var raw = Number(national[el.getAttribute("data-ph-percent")]);
                el.textContent = formatPercent(raw);
                var bar = el.parentElement.querySelector("[data-ph-bar]");
                if (bar && isFinite(raw)) {
                    bar.style.width = Math.max(0, Math.min(100, raw)) + "%";
                }
            });
        })
        .catch(function () {
            root.querySelectorAll("[data-ph-count], [data-ph-percent]").forEach(function (el) {
                el.textContent = "—";
            });
        });
})();
