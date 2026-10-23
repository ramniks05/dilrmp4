$(document).ready(function () {
    document.getElementById("maptitle").innerHTML = "Computerization of Land Records: % of villages completed";
    document.getElementById("tabletitle").innerHTML = "Computerization of Land Records: % of villages completed";
    statemap("0", 0, 0, 0, 0);
    getAllMapDataChartforCLR();
});
function loadMatribhoomiData(anchor) {
    if (typeof anchor === "string") {
        anchor = document.getElementById(anchor);
    }
    console.log(anchor);
    id=anchor.id;
    document.getElementById("mapDiv").innerHTML = "";
    document.getElementById("maptitle").innerHTML = "";
    document.getElementById("tabletitle").innerHTML = "";
    if (id === "clr") {
        document.getElementById("maptitle").innerHTML = "Computerization of Land Records: % of villages completed";
        document.getElementById("tabletitle").innerHTML = "Computerization of Land Records: % of villages completed";
        statemap("0", 0, 0, 0, 0);
        getAllMapDataChartforCLR();
    } else if (id === "MapDigitization") {
        document.getElementById("maptitle").innerHTML = "Map digitization: % of maps / FMBs / tippans digitized";
        document.getElementById("tabletitle").innerHTML = "Map digitization: % of maps / FMBs / tippans digitized";
        statemap("30", 0, 0, 0, 0);
        getAllMapDataChartforMapDigitization();
    }
    else if (id === "mrr") {
        document.getElementById("maptitle").innerHTML = "Modern Record Room (Tehsil): % of tehsils with completed MRR";
        document.getElementById("tabletitle").innerHTML = "Modern Record Room (Tehsil): % of tehsils with completed MRR";
        statemap("6", 0, 0, 0, 0);
        getAllMapDataChartforMRR();
    }
    else if (id === "surveyReservey") {
        document.getElementById("maptitle").innerHTML = "Survey / Re-survey: % of villages with final publication of records";
        document.getElementById("tabletitle").innerHTML = "Survey / Re-survey: % of villages with final publication of records";
        statemap("40", 0, 0, 0, 0);
        getAllMapDataChartforSurveyReservey();
    }
    else if (id === "sro") {
        document.getElementById("maptitle").innerHTML = "Computerization of Registration Process: % of SROs computerized";
        document.getElementById("tabletitle").innerHTML = "Computerization of Registration Process: % of SROs computerized";
        statemap("50", 0, 0, 0, 0);
        getAllMapDataChartforSro();
    }
    else if (id === "srolr") {
        document.getElementById("maptitle").innerHTML = "Percentage of SRO Integrated with Land Record";
        document.getElementById("tabletitle").innerHTML = "Percentage of SRO Integrated with Land Record";
        statemap("60", 0, 0, 0, 0);
        getAllMapDataChartforSroLr();
    }
    else if (id === "cadastralMaplwror") {
        document.getElementById("maptitle").innerHTML = "Cadastral Maps linked to Record of Rights (Villages)";
        document.getElementById("tabletitle").innerHTML = "Cadastral Maps linked to Record of Rights (Villages)";
        statemap("70", 0, 0, 0, 0);
        getAllMapDataChartforVillagesCadastralMapLinkedwithRoR();
    }
    else if (id === "rccms") {
        document.getElementById("maptitle").innerHTML = "Computerization of Revenue Courts: % of courts computerized";
        document.getElementById("tabletitle").innerHTML = "Computerization of Revenue Courts: % of courts computerized";
        statemap("80", 0, 0, 0, 0);
        getAllMapDataChartforRCCMS();
    }
    else if (id === "linkedwithaadhaar") {
        document.getElementById("maptitle").innerHTML = "Consent-based Aadhaar integration: % of villages with all RoRs linked";
        document.getElementById("tabletitle").innerHTML = "Consent-based Aadhaar integration: % of villages with all RoRs linked";
        statemap("90", 0, 0, 0, 0);
        getAllMapDataChartforAadhar();
    }
    handleBoxClick(anchor);
}


/* Lets a page react to State/UT clicks and zoom requests. Pages that do not define
   window.hpMapStateClick keep the existing behaviour. */
/* BharatMaps Admin Boundary District service: layer 0 = State, layer 1 = District. */
var HP_BOUNDARY_SERVICE_URL = 'https://mapservice.gov.in/gismapservice/rest/services/BharatMapService/Admin_Boundary_Village/MapServer';
var HP_BOUNDARY_SERVICE_TOKEN = 'RC8vcGojsNt3GLCKzRWLKMVSh4e-HQVPx3QaXecXrUwIFIB37XRG_aJwIsX6bMd7';

var hpStateMap = { map: null, layer: null, extent: null, districtLayer: null };
var HP_INDIA_EXTENT = { xmin: 7189291.79, ymin: 563260.13, xmax: 11244335.73, ymax: 4780505.83, spatialReference: { wkid: 102100 } };

/* Draws the district boundaries of one State/UT. percentByLgd maps district LGD code to a percentage
   used for the fill colour; districts without a value are drawn as outlines only. */
function hpShowDistricts(stateId, percentByLgd, title, onDrawn) {
    hpClearDistricts();
    if (!hpStateMap.map || !/^\d+$/.test(String(stateId))) { return; }
    var map = hpStateMap.map;
    require([
        "esri/layers/FeatureLayer", "esri/renderers/UniqueValueRenderer", "esri/symbols/SimpleFillSymbol",
        "esri/symbols/SimpleLineSymbol", "esri/Color", "esri/graphic"
    ], function (FeatureLayer, UniqueValueRenderer, SimpleFillSymbol, SimpleLineSymbol, Color, Graphic) {
        var outline = new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color([1, 44, 111, 0.9]), 1.2);
        var renderer = new UniqueValueRenderer(new SimpleFillSymbol(SimpleFillSymbol.STYLE_SOLID, outline, new Color([255, 255, 255, 0.35])), "dist_lgd");
        var values = percentByLgd || {};
        Object.keys(values).forEach(function (lgd) {
            if (values[lgd] == null) { return; }
            var fill = new Color(fillColorOnMap(Math.min(100, Number(values[lgd]))));
            fill.a = 0.85;
            renderer.addValue(String(lgd), new SimpleFillSymbol(SimpleFillSymbol.STYLE_SOLID, outline, fill));
        });
        var layer = new FeatureLayer(HP_BOUNDARY_SERVICE_URL + "/1?Token=" + HP_BOUNDARY_SERVICE_TOKEN, {
            mode: FeatureLayer.MODE_SNAPSHOT,
            outFields: ["dtname", "dist_lgd", "state_lgd"]
        });
        layer.setDefinitionExpression("state_lgd = " + Number(stateId));
        layer.setRenderer(renderer);
        // The service only draws districts between 1:1,000,000 and 1:500,000; a whole State/UT is smaller scale.
        layer.on("load", function () { layer.setScaleRange(0, 0); });
        var highlight = new SimpleFillSymbol(SimpleFillSymbol.STYLE_NULL,
            new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color("#fa8c16"), 3), null);
        layer.on("mouse-over", function (evt) {
            var a = evt.graphic.attributes;
            var p = values[a.dist_lgd];
            map.graphics.clear();
            map.graphics.add(new Graphic(evt.graphic.geometry, highlight));
            map.infoWindow.setTitle(title || "District");
            map.infoWindow.setContent("<div>District : <b>" + a.dtname + "</b><br>Percentage % : <b>"
                + (p == null ? "Not reported" : Number(p).toFixed(2)) + "</b></div>");
            map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
        });
        layer.on("mouse-out", function () {
            map.graphics.clear();
            map.infoWindow.hide();
        });
        if (typeof onDrawn === "function") {
            var drawn = layer.on("update-end", function () {
                drawn.remove();
                onDrawn();
            });
        }
        hpStateMap.districtLayer = layer;
        map.addLayer(layer);
        hpBringHighlightsToTop();
    });
}

function hpClearDistricts() {
    if (hpStateMap.map && hpStateMap.districtLayer) {
        hpStateMap.map.removeLayer(hpStateMap.districtLayer);
    }
    if (hpStateMap.map && hpStateMap.selectLayer) {
        hpStateMap.map.removeLayer(hpStateMap.selectLayer);
    }
    hpStateMap.districtLayer = null;
    hpStateMap.selectLayer = null;
}

/* statemap() builds each map asynchronously; a map that finishes after a newer request would draw on top of it. */
var hpMapGeneration = 0;

/* Map.destroy() looks nodes up by id, and every map here shares the "mapDiv" ids, so it could remove
   the current map. Detach only the stale map's own root node instead. */
function hpRetireMap(map) {
    if (!map) { return; }
    try { map.removeAllLayers(); } catch (e) { /* already torn down */ }
    if (map.root && map.root.parentNode) {
        map.root.parentNode.removeChild(map.root);
    }
}

function registerStateLayer(map, layer, generation) {
    if (generation !== undefined && generation !== hpMapGeneration) {
        hpRetireMap(map);
        return;
    }
    if (hpStateMap.map && hpStateMap.map !== map) {
        hpRetireMap(hpStateMap.map);
    }
    hpStateMap = { map: map, layer: layer, extent: null, districtLayer: null, zoomedState: null };
    hpDecoratePopup(map);
    map.on("load", function () {
        if (typeof window.hpMapStateClick !== "function") {
            hpStateMap.extent = map.extent;
            return;
        }
        hpEnableMapNavigation(map);
        require(["esri/geometry/Extent"], function (Extent) {
            hpStateMap.extent = new Extent(HP_INDIA_EXTENT);
            if (!window.hpSelectedStateId) {
                map.setExtent(hpStateMap.extent, true);
            }
        });
    });
    // Hover tooltips and the district overlay sit above the State layer and swallow its clicks,
    // so the clicked State/UT is found from the map point instead.
    map.on("click", function (evt) {
        if (typeof window.hpMapStateClick !== "function" || !evt.mapPoint) { return; }
        map.infoWindow.hide();
        var hit = hpStateAt(evt.mapPoint);
        if (hit) {
            if (String(hit.attributes.State_LGD) !== String(window.hpSelectedStateId)) {
                zoomToStateGraphic(hit);
                window.hpMapStateClick(String(hit.attributes.State_LGD), hit.attributes.STNAME);
            } else {
                var district = hpDistrictAt(evt.mapPoint);
                if (district && typeof window.hpMapDistrictClick === "function") {
                    hpHighlightDistrict(district.attributes.dist_lgd);
                    window.hpMapDistrictClick(String(district.attributes.dist_lgd), district.attributes.dtname);
                }
            }
        } else if (window.hpSelectedStateId && typeof window.hpMapBackToIndia === "function") {
            window.hpMapBackToIndia();
        }
    });
    // A selected State/UT is zoomed once its shape has been drawn, including after the map is rebuilt.
    layer.on("update-end", function () {
        var id = window.hpSelectedStateId;
        if (id && hpStateMap.zoomedState !== String(id) && hpZoomToState(id)
                && typeof window.hpOnStateLayerReady === "function") {
            window.hpOnStateLayerReady(id);
        }
    });
}

function hpDistrictAt(point) {
    var graphics = (hpStateMap.districtLayer && hpStateMap.districtLayer.graphics) || [];
    for (var i = 0; i < graphics.length; i++) {
        var g = graphics[i];
        if (g.attributes && g.geometry && g.geometry.contains && g.geometry.contains(point)) {
            return g;
        }
    }
    return null;
}

/* Keeps a saffron outline around the selected district; pass null to clear it. */
function hpHighlightDistrict(districtLgd) {
    var map = hpStateMap.map;
    if (!map) { return; }
    require(["esri/layers/GraphicsLayer", "esri/graphic", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol", "esri/Color"],
        function (GraphicsLayer, Graphic, SimpleFillSymbol, SimpleLineSymbol, Color) {
            if (!hpStateMap.selectLayer) {
                hpStateMap.selectLayer = new GraphicsLayer();
                map.addLayer(hpStateMap.selectLayer);
            }
            hpStateMap.selectLayer.clear();
            if (districtLgd == null || !hpStateMap.districtLayer) { return; }
            var graphics = hpStateMap.districtLayer.graphics || [];
            for (var i = 0; i < graphics.length; i++) {
                if (String(graphics[i].attributes.dist_lgd) === String(districtLgd)) {
                    hpStateMap.selectLayer.add(new Graphic(graphics[i].geometry, new SimpleFillSymbol(SimpleFillSymbol.STYLE_SOLID,
                        new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color("#1d4ed8"), 2), new Color([29, 78, 216, 0.14]))));
                    break;
                }
            }
            hpBringHighlightsToTop();
        });
}

/* Thin saffron outline around the selected State/UT; pass null to clear. */
function hpHighlightState(geometry) {
    var map = hpStateMap.map;
    if (!map) { return; }
    require(["esri/layers/GraphicsLayer", "esri/graphic", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol", "esri/Color"],
        function (GraphicsLayer, Graphic, SimpleFillSymbol, SimpleLineSymbol, Color) {
            if (hpStateMap.map !== map) { return; }
            if (!hpStateMap.stateSelectLayer) {
                hpStateMap.stateSelectLayer = new GraphicsLayer();
                map.addLayer(hpStateMap.stateSelectLayer);
            }
            hpStateMap.stateSelectLayer.clear();
            if (!geometry) { return; }
            hpStateMap.stateSelectLayer.add(new Graphic(geometry, new SimpleFillSymbol(SimpleFillSymbol.STYLE_NULL,
                new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color("#f57c00"), 2.5), null)));
            hpBringHighlightsToTop();
        });
}

/* District fills are added after the outlines, so move the outlines back on top (State/UT, then district). */
function hpBringHighlightsToTop() {
    var map = hpStateMap.map;
    if (!map) { return; }
    [hpStateMap.stateSelectLayer, hpStateMap.selectLayer].forEach(function (layer) {
        if (layer && map.getLayer(layer.id)) {
            map.reorderLayer(layer, map.graphicsLayerIds.length - 1);
        }
    });
}

/* Home page only: the component maps build their hover text as "Label : value" lines; this turns that
   text into a card (name, figures, coloured percentage bar) without touching each component's code. */
function hpDecoratePopup(map) {
    var iw = map.infoWindow;
    if (!iw || iw.hpDecorated) { return; }
    iw.hpDecorated = true;
    var setContent = iw.setContent;
    iw.setContent = function (content) {
        if (typeof window.hpMapStateClick === "function" && typeof content === "string") {
            if (iw.domNode && iw.domNode.classList) { iw.domNode.classList.add("hp-popup"); }
            content = hpFormatPopup(content);
        }
        return setContent.call(iw, content);
    };
}

function hpFormatPopup(html) {
    var esc = function (s) {
        return String(s).replace(/[&<>"']/g, function (c) { return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]; });
    };
    var name = null, pct = null, rows = [];
    String(html).replace(/^\s*<div>/i, "").replace(/<\/div>\s*$/i, "").split(/<br\s*\/?>/i).forEach(function (part) {
        var holder = document.createElement("div");
        holder.innerHTML = part;
        var text = (holder.textContent || "").replace(/\s+/g, " ").trim();
        var at = text.indexOf(":");
        if (at < 0) { return; }
        var label = text.slice(0, at).trim(), value = text.slice(at + 1).trim();
        if (value === "" || /^(undefined|null|NaN)$/i.test(value)) { value = "Not reported"; }
        if (/^(state\s*name|district)$/i.test(label)) { name = value; }
        else if (/percent/i.test(label)) { pct = value; }
        else { rows.push([label, value]); }
    });
    if (name === null && pct === null && !rows.length) { return html; }

    var out = '<div class="hp-pop">';
    if (name !== null) { out += '<div class="hp-pop__name">' + esc(name) + '</div>'; }
    if (pct !== null) {
        var p = parseFloat(pct);
        if (isNaN(p)) {
            out += '<div class="hp-pop__pct is-empty">' + esc(pct) + '</div>';
        } else {
            var shown = Math.max(0, Math.min(100, p));
            out += '<div class="hp-pop__pct"><b>' + p.toFixed(2) + '%</b><span class="hp-pop__bar"><i style="width:' + shown
                + '%;background:' + fillColorOnMap(p) + '"></i></span></div>';
        }
    }
    if (rows.length) {
        out += '<dl class="hp-pop__rows">';
        rows.forEach(function (r) {
            var v = r[1];
            if (/^-?\d+(\.\d+)?$/.test(v)) { v = Number(v).toLocaleString("en-IN"); }
            out += '<div><dt>' + esc(r[0]) + '</dt><dd>' + esc(v) + '</dd></div>';
        });
        out += '</dl>';
    }
    return out + '</div>';
}

/* Zooms to one district of the selected State/UT once the district layer has drawn it. */
function hpZoomToDistrict(districtLgd) {
    var map = hpStateMap.map;
    var graphics = (hpStateMap.districtLayer && hpStateMap.districtLayer.graphics) || [];
    for (var i = 0; i < graphics.length; i++) {
        if (String(graphics[i].attributes.dist_lgd) === String(districtLgd) && graphics[i].geometry) {
            map.setExtent(graphics[i].geometry.getExtent().expand(1.8));
            return true;
        }
    }
    return false;
}

/* State/UT map for components that /api/chart does not serve. percentByState maps State LGD code to a percentage. */
function hpGenericStateMap(title, percentByState) {
    var hpGen = ++hpMapGeneration;
    document.getElementById("mapDiv").innerHTML = "";
    document.getElementById("maptitle").innerHTML = title;
    var tableTitle = document.getElementById("tabletitle");
    if (tableTitle) { tableTitle.innerHTML = title; }
    require([
        "esri/map", "esri/layers/FeatureLayer", "esri/renderers/UniqueValueRenderer", "esri/symbols/SimpleFillSymbol",
        "esri/symbols/SimpleLineSymbol", "esri/Color", "esri/geometry/Extent", "esri/graphic", "dojo/domReady!"
    ], function (Map, FeatureLayer, UniqueValueRenderer, SimpleFillSymbol, SimpleLineSymbol, Color, Extent, Graphic) {
        if (hpGen !== hpMapGeneration) { return; }
        var map = new Map("mapDiv", { extent: new Extent(HP_INDIA_EXTENT), slider: false, maxScale: 500, minScale: 56000000, showLabels: true });
        map.on("load", function () {
            map.disableScrollWheelZoom();
            map.disableDoubleClickZoom();
            map.disableMapNavigation();
            map.disableRubberBandZoom();
            map.disablePan();
            map.disableKeyboardNavigation();
            map.disableShiftDoubleClickZoom();
        });
        var values = percentByState || {};
        var outline = new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color("#4E79BE"), 0.7);
        var renderer = new UniqueValueRenderer(new SimpleFillSymbol(SimpleFillSymbol.STYLE_SOLID, outline, new Color("#e5e5e5")), "State_LGD");
        Object.keys(values).forEach(function (id) {
            if (values[id] == null) { return; }
            renderer.addValue(String(id), new SimpleFillSymbol(SimpleFillSymbol.STYLE_SOLID, outline,
                new Color(fillColorOnMap(Math.min(100, Number(values[id]))))));
        });
        var stateLayer = new FeatureLayer(HP_BOUNDARY_SERVICE_URL + "/0?Token=" + HP_BOUNDARY_SERVICE_TOKEN, {
            mode: FeatureLayer.MODE_ONDEMAND,
            outFields: ["STNAME", "State_LGD"]
        });
        stateLayer.setRenderer(renderer);
        var highlight = new SimpleFillSymbol(SimpleFillSymbol.STYLE_SOLID,
            new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color("#008000"), 3), new Color([125, 125, 125, 0.35]));
        stateLayer.on("mouse-over", function (evt) {
            var a = evt.graphic.attributes;
            var p = values[a.State_LGD];
            map.graphics.clear();
            map.graphics.add(new Graphic(evt.graphic.geometry, highlight));
            map.infoWindow.setTitle(title);
            map.infoWindow.setContent('<div>StateName : <font color="orange">' + a.STNAME + '</font><br>Percentage % : <font color="orange">'
                + (p == null ? "Not reported" : Number(p).toFixed(2)) + '</font></div>');
            map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
        });
        stateLayer.on("mouse-out", function () {
            map.graphics.clear();
            map.infoWindow.hide();
        });
        map.addLayers([stateLayer]);
        registerStateLayer(map, stateLayer, hpGen);
    });
}

function hpStateAt(point) {
    var graphics = (hpStateMap.layer && hpStateMap.layer.graphics) || [];
    for (var i = 0; i < graphics.length; i++) {
        var g = graphics[i];
        if (g.attributes && g.geometry && g.geometry.contains && g.geometry.contains(point)) {
            return g;
        }
    }
    return null;
}

function zoomToStateGraphic(graphic) {
    if (hpStateMap.map && graphic && graphic.geometry) {
        hpStateMap.map.setExtent(graphic.geometry.getExtent().expand(1.3));
    }
}

function hpZoomToState(stateId) {
    var layer = hpStateMap.layer;
    if (!layer || !layer.graphics) { return false; }
    for (var i = 0; i < layer.graphics.length; i++) {
        var g = layer.graphics[i];
        if (g.attributes && String(g.attributes.State_LGD) === String(stateId)) {
            zoomToStateGraphic(g);
            hpStateMap.zoomedState = String(stateId);
            hpHighlightState(g.geometry);
            return true;
        }
    }
    if (!layer.loaded || !/^\d+$/.test(String(stateId))) { return false; }
    // The State layer only holds the States/UTs currently in view, so fetch an off-screen one from the service.
    var map = hpStateMap.map;
    hpStateMap.zoomedState = String(stateId);
    require(["esri/tasks/query"], function (Query) {
        var q = new Query();
        q.where = "State_LGD = " + Number(stateId);
        q.returnGeometry = true;
        q.outFields = ["State_LGD"];
        q.outSpatialReference = map.spatialReference;
        layer.queryFeatures(q, function (result) {
            var feature = result && result.features && result.features[0];
            if (!feature || hpStateMap.map !== map || String(window.hpSelectedStateId) !== String(stateId)) { return; }
            map.setExtent(feature.geometry.getExtent().expand(1.3));
            hpHighlightState(feature.geometry);
        });
    });
    return true;
}

/* Home page only: each statemap() branch switches navigation off on load; this load handler runs after
   those, so panning and zooming come back without touching the shared per-component code. Double-click
   zoom stays off because a click selects a State/UT; wheel zoom stays off so the mouse wheel scrolls the
   page (the +/- buttons zoom instead). */
function hpEnableMapNavigation(map) {
    // The individual enableX() calls only reconnect handlers while map navigation as a whole is on.
    map.enableMapNavigation();
    map.enablePan();
    map.enableRubberBandZoom();
    map.enableKeyboardNavigation();
    if (typeof map.enablePinchZoom === "function") { map.enablePinchZoom(); }
    map.disableScrollWheelZoom();
    map.disableDoubleClickZoom();
    map.disableShiftDoubleClickZoom();
}

var HP_MIN_SCALE = 2000;
var HP_MAX_SCALE = 60000000;

/* Zooms by factor (< 1 zooms in, > 1 out) keeping anchor (a map point) fixed on screen; centre if omitted. */
function hpZoomBy(factor, anchor) {
    var map = hpStateMap.map;
    if (!map || !map.extent) { return; }
    var scale = map.getScale();
    if (factor > 1 && scale * factor > HP_MAX_SCALE) {
        if (hpStateMap.extent) { map.setExtent(hpStateMap.extent, true); }
        return;
    }
    if (factor < 1 && scale * factor < HP_MIN_SCALE) { return; }
    var e = map.extent;
    var p = anchor || e.getCenter();
    require(["esri/geometry/Extent"], function (Extent) {
        map.setExtent(new Extent(p.x - (p.x - e.xmin) * factor, p.y - (p.y - e.ymin) * factor,
            p.x + (e.xmax - p.x) * factor, p.y + (e.ymax - p.y) * factor, e.spatialReference));
    });
}

function hpResetMapExtent() {
    hpClearDistricts();
    if (hpStateMap.stateSelectLayer) { hpStateMap.stateSelectLayer.clear(); }
    hpStateMap.zoomedState = null;
    if (hpStateMap.map && hpStateMap.extent) {
        hpStateMap.map.setExtent(hpStateMap.extent, true);
    }
}

//var misUrl = "https://dilrmp.gov.in/bhoomisammannew/newDashboard?";
var misUrl = "/api/chart/";

function fillColorOnMap(j) {
    let color;

    if (j >= 0 && j <= 30) {
        color = '#FF0000';
    } else if (j > 30 && j <= 65) {
        color = '#FF9A00';
    } else if (j > 65 && j <= 80) {
        color = '#FFDB00';
    } else if (j > 80 && j <= 100) {
        color = '#059212';
    } else {
        color = '#FF0000';
    }
    return color;

}



function statemap(cat, xmin, ymin, xmax, ymax) {
    var hpGen = ++hpMapGeneration;
    var serviceUrl = HP_BOUNDARY_SERVICE_URL;
    var serviceUrlToken = '?Token=' + HP_BOUNDARY_SERVICE_TOKEN;
   // var serviceUrlToken = '?Token=vjy7e0tKr_2n_-Etb0dkbPk-CzVfD-zWGgnmkaMXPVDDwY6P1WV6RsBvv94w3TGElGUBj1zH3CkS5xvlYF2AEg..';





    if (cat === "0") {
        var map;
        var mapId = "mapDiv";
        require([
            "dojo/dom-construct", "esri/dijit/BasemapGallery", "esri/dijit/BasemapToggle", "dijit/dijit",
            "esri/map", "esri/geometry/webMercatorUtils",
            "esri/layers/FeatureLayer",
            "esri/dijit/Legend", "esri/dijit/Search", "esri/renderers/ScaleDependentRenderer",
            "esri/symbols/Font", "esri/geometry/Point",
            "esri/SpatialReference", "esri/graphic", "esri/lang",
            "esri/dijit/PopupTemplate", "dijit/Dialog", "dijit/TooltipDialog",
            "esri/renderers/UniqueValueRenderer", "esri/symbols/TextSymbol",
            "dijit/registry", "dijit/form/Button", "dijit/TooltipDialog",
            "dijit/popup", "esri/arcgis/utils",
            "esri/geometry/Extent",
            "esri/InfoTemplate", "esri/symbols/SimpleMarkerSymbol",
            "esri/symbols/PictureMarkerSymbol", "esri/symbols/SimpleLineSymbol",
            "esri/symbols/SimpleFillSymbol", "esri/renderers/SimpleRenderer",
            "esri/Color", "dojo/number", "dojo/dom-style",
            "dojo/domReady!"
        ], function (
            domConstruct, BasemapGallery, BasemapToggle, dijit,
            Map, webMercatorUtils, FeatureLayer, Legend,
            Search, ScaleDependentRenderer, Font, Point, SpatialReference,
            Graphic, esriLang, PopupTemplate, Dialog, TooltipDialog,
            UniqueValueRenderer, TextSymbol, registry,
            Button, TooltipDialog, popup,
            utils,
            Extent,
            InfoTemplate, SimpleMarkerSymbol, PictureMarkerSymbol,
            SimpleLineSymbol, SimpleFillSymbol,
            SimpleRenderer, Color, number, Style
        ) {

            var bounds = new Extent({
                "xmin": 7189291.79,
                "ymin": 563260.13,
                "xmax": 11244335.73,
                "ymax": 4780505.83,
                "spatialReference": { "wkid": 102100 }
            });
            var map = new Map(mapId, {
                extent: bounds,
                zoom: 5,
                slider: false,
                "maxScale": 500, //0
                "minScale": 56000000, // 100000000/56000000
                showLabels: true
            });
            var stateLayer;
            var stateBoundry;
            var poupdata;
            var clrdata;
            var statesColor = new Color('#4E79BE');


            //tokens in newDashboard for local systems

            var origin = window.location.origin;

            stateLayer = new FeatureLayer(serviceUrl + '/0' + serviceUrlToken, {
                mode: FeatureLayer.MODE_ONDEMAND,
                outFields: ["STNAME", "STCODE11"],
                displayField: "STNAME",
                showLabels: true,
            });
            var defaultSymbol = new SimpleFillSymbol().setStyle(SimpleFillSymbol.STYLE_NULL);
            defaultSymbol.outline.setStyle(SimpleLineSymbol.STYLE_NULL);
            var renderer = new UniqueValueRenderer(defaultSymbol, "State_LGD");
            map.on("load", function () {
                map.disableScrollWheelZoom();
                map.disableDoubleClickZoom();
                map.disableMapNavigation();
                map.disableRubberBandZoom();
                map.disablePan();
                map.disableKeyboardNavigation();
                map.disableShiftDoubleClickZoom();

            });
            map.on("click", function (evt) {
                return false;
            });
            var clr;

            $.ajax({
                type: 'POST',
                url: misUrl + 'category=11',
                dataType: 'json',
                async: false,
                success: function (data) {
                    clrdata = data;
                    for (var i = 0; i < data.length; i++) {
                        var scode = data[i].lgd.toString();
                        var j = data[i].percent.toString();
                        const selectedColor = fillColorOnMap(j);
                        renderer.addValue(scode, new SimpleFillSymbol(
                            "solid",
                            new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color(statesColor), 0.7),
                            new Color(selectedColor)
                        ));
                    }
                }
            });
            stateLayer.on("mouse-over", showTooltip);
            stateLayer.on("mouse-out", closeDialog);
            var highlightSymbol = new SimpleFillSymbol(
                SimpleFillSymbol.STYLE_SOLID,
                new SimpleLineSymbol(
                    SimpleLineSymbol.STYLE_SOLID,
                    new Color("#008000"), 3
                ),
                new Color([125, 125, 125, 0.35])
            );
            function showTooltip(evt) {
                closeDialog();
                var total, completed, percentt;
                var stname = evt.graphic.attributes.STNAME;
                for (var i = 0; i < clrdata.length; i++) {
                    var dcode = clrdata[i].lgd.toString();
                    if (dcode == evt.graphic.attributes.State_LGD) {
                        total = parseInt(clrdata[i].total.toString());
                        completed = parseInt(clrdata[i].completed.toString());
                        if (completed == null || completed == 0) {
                            percentt = '0';
                        } else {
                            percentt = (completed / total) * 100;
                            percentt = percentt.toFixed(2);
                        }
                        break;
                    }
                }
                var highlightGraphic = new Graphic(evt.graphic.geometry, highlightSymbol);
                map.graphics.add(highlightGraphic);
                var table = "";
                table = '<div>' + 'StateName' + ' : <font color="orange">' + stname + '</font><br>' + 'Total (Villages)' + ' : <font color="orange">' + total + '</font><br>' + 'Completed (Villages)' + ': <font color="orange">'
                    + completed + '</font><br>' + 'Percentage %' + ' : <font color="orange">' + percentt + '</font></div>';
                map.infoWindow.setTitle("Computerization of Land Records");
                map.infoWindow.setContent(table);
                map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
            }
            function closeDialog() {
                map.graphics.clear();
                var widget = dijit.byId("tooltipDialog");
                if (widget) {
                    widget.destroy();
                }
            }
            stateLayer.redraw();
            stateLayer.setRenderer(renderer);
            map.addLayers([stateLayer]);
            registerStateLayer(map, stateLayer, hpGen);
        });
    }
    else if (cat === "30") {

        var map;
        var mapId = "mapDiv";
        require([
            "dojo/dom-construct", "esri/dijit/BasemapGallery", "esri/dijit/BasemapToggle", "dijit/dijit",
            "esri/map", "esri/geometry/webMercatorUtils",
            "esri/layers/FeatureLayer",
            "esri/dijit/Legend", "esri/dijit/Search", "esri/renderers/ScaleDependentRenderer",
            "esri/symbols/Font", "esri/geometry/Point",
            "esri/SpatialReference", "esri/graphic", "esri/lang",
            "esri/dijit/PopupTemplate", "dijit/Dialog", "dijit/TooltipDialog",
            "esri/renderers/UniqueValueRenderer", "esri/symbols/TextSymbol",
            "dijit/registry", "dijit/form/Button", "dijit/TooltipDialog",
            "dijit/popup", "esri/arcgis/utils",
            "esri/geometry/Extent",
            "esri/InfoTemplate", "esri/symbols/SimpleMarkerSymbol",
            "esri/symbols/PictureMarkerSymbol", "esri/symbols/SimpleLineSymbol",
            "esri/symbols/SimpleFillSymbol", "esri/renderers/SimpleRenderer",
            "esri/Color", "dojo/number", "dojo/dom-style",
            "dojo/domReady!"
        ], function (
            domConstruct, BasemapGallery, BasemapToggle, dijit,
            Map, webMercatorUtils, FeatureLayer, Legend,
            Search, ScaleDependentRenderer, Font, Point, SpatialReference,
            Graphic, esriLang, PopupTemplate, Dialog, TooltipDialog,
            UniqueValueRenderer, TextSymbol, registry,
            Button, TooltipDialog, popup,
            utils,
            Extent,
            InfoTemplate, SimpleMarkerSymbol, PictureMarkerSymbol,
            SimpleLineSymbol, SimpleFillSymbol,
            SimpleRenderer, Color, number, Style
        ) {

            var bounds = new Extent({
                "xmin": 7189291.79,
                "ymin": 563260.13,
                "xmax": 11244335.73,
                "ymax": 4780505.83,
                "spatialReference": { "wkid": 102100 }
            });
            var map = new Map(mapId, {
                extent: bounds,
                zoom: 5,
                slider: false,
                "maxScale": 500, //0
                "minScale": 56000000, // 100000000/56000000
                showLabels: true
            });
            var stateLayer;
            var stateBoundry;
            var poupdata;
            var mrrdata;
            var statesColor = new Color('#4E79BE');



            //tokens in newDashboard for local systems

            var origin = window.location.origin;

            stateLayer = new FeatureLayer(serviceUrl + '/0' + serviceUrlToken, {
                mode: FeatureLayer.MODE_ONDEMAND,
                outFields: ["STNAME", "STCODE11"],
                displayField: "STNAME",
                showLabels: true,
            });
            var defaultSymbol = new SimpleFillSymbol().setStyle(SimpleFillSymbol.STYLE_NULL);
            defaultSymbol.outline.setStyle(SimpleLineSymbol.STYLE_NULL);
            var renderer = new UniqueValueRenderer(defaultSymbol, "State_LGD");
            map.on("load", function () {
                map.disableScrollWheelZoom();
                map.disableDoubleClickZoom();
                map.disableMapNavigation();
                map.disableRubberBandZoom();
                map.disablePan();
                map.disableKeyboardNavigation();
                map.disableShiftDoubleClickZoom();

            });
            map.on("click", function (evt) {
                return false;
            });
            $.ajax({
                type: 'POST',
                url: misUrl + 'category=30',
                dataType: 'json',
                async: false,
                success: function (data) {
                    clrdata = data;
                    for (var i = 0; i < data.length; i++) {
                        var scode = data[i].lgd.toString();
                        var j = data[i].percent.toString();
                        const selectedColor = fillColorOnMap(j);
                        renderer.addValue(scode, new SimpleFillSymbol(
                            "solid",
                            new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color(statesColor), 0.7),
                            new Color(selectedColor)
                        ));
                    }
                }
            });
            stateLayer.on("mouse-over", showTooltip);
            stateLayer.on("mouse-out", closeDialog);
            var highlightSymbol = new SimpleFillSymbol(
                SimpleFillSymbol.STYLE_SOLID,
                new SimpleLineSymbol(
                    SimpleLineSymbol.STYLE_SOLID,
                    new Color("#008000"), 3
                ),
                new Color([125, 125, 125, 0.35])
            );
            function showTooltip(evt) {
                closeDialog();
                var total, completed, percentt;
                var stname = evt.graphic.attributes.STNAME;
                for (var i = 0; i < clrdata.length; i++) {
                    var dcode = clrdata[i].lgd.toString();
                    if (dcode == evt.graphic.attributes.State_LGD) {
                        total = parseInt(clrdata[i].total.toString());
                        completed = parseInt(clrdata[i].completed.toString());
                        if (completed == null || completed == 0) {
                            percentt = '0';
                        } else {
                            percentt = (completed / total) * 100;
                            percentt = percentt.toFixed(2);
                        }
                        break;
                    }
                }
                var highlightGraphic = new Graphic(evt.graphic.geometry, highlightSymbol);
                map.graphics.add(highlightGraphic);
                var table = "";
                table = '<div>' + 'StateName' + ' : <font color="orange">' + stname + '</font><br>' + 'Total (MapSheets)' + ' : <font color="orange">' + total + '</font><br>' + 'Digitized (MapSheets)' + ': <font color="orange">'
                    + completed + '</font><br>' + 'Percentage %' + ' : <font color="orange">' + percentt + '</font></div>';
                map.infoWindow.setTitle("Map Digitization");
                map.infoWindow.setContent(table);
                map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
            }
            function closeDialog() {
                map.graphics.clear();
                var widget = dijit.byId("tooltipDialog");
                if (widget) {
                    widget.destroy();
                }
            }
            stateLayer.redraw();
            stateLayer.setRenderer(renderer);
            map.addLayers([stateLayer]);
            registerStateLayer(map, stateLayer, hpGen);
        });
    } else if (cat === "6") {
        var map;
        var mapId = "mapDiv";
        require([
            "dojo/dom-construct", "esri/dijit/BasemapGallery", "esri/dijit/BasemapToggle", "dijit/dijit",
            "esri/map", "esri/geometry/webMercatorUtils",
            "esri/layers/FeatureLayer",
            "esri/dijit/Legend", "esri/dijit/Search", "esri/renderers/ScaleDependentRenderer",
            "esri/symbols/Font", "esri/geometry/Point",
            "esri/SpatialReference", "esri/graphic", "esri/lang",
            "esri/dijit/PopupTemplate", "dijit/Dialog", "dijit/TooltipDialog",
            "esri/renderers/UniqueValueRenderer", "esri/symbols/TextSymbol",
            "dijit/registry", "dijit/form/Button", "dijit/TooltipDialog",
            "dijit/popup", "esri/arcgis/utils",
            "esri/geometry/Extent",
            "esri/InfoTemplate", "esri/symbols/SimpleMarkerSymbol",
            "esri/symbols/PictureMarkerSymbol", "esri/symbols/SimpleLineSymbol",
            "esri/symbols/SimpleFillSymbol", "esri/renderers/SimpleRenderer",
            "esri/Color", "dojo/number", "dojo/dom-style",
            "dojo/domReady!"
        ], function (
            domConstruct, BasemapGallery, BasemapToggle, dijit,
            Map, webMercatorUtils, FeatureLayer, Legend,
            Search, ScaleDependentRenderer, Font, Point, SpatialReference,
            Graphic, esriLang, PopupTemplate, Dialog, TooltipDialog,
            UniqueValueRenderer, TextSymbol, registry,
            Button, TooltipDialog, popup,
            utils,
            Extent,
            InfoTemplate, SimpleMarkerSymbol, PictureMarkerSymbol,
            SimpleLineSymbol, SimpleFillSymbol,
            SimpleRenderer, Color, number, Style
        ) {

            var bounds = new Extent({
                "xmin": 7189291.79,
                "ymin": 563260.13,
                "xmax": 11244335.73,
                "ymax": 4780505.83,
                "spatialReference": { "wkid": 102100 }
            });
            var map = new Map(mapId, {
                extent: bounds,
                zoom: 5,
                slider: false,
                "maxScale": 500, //0
                "minScale": 56000000, // 100000000/56000000
                showLabels: true
            });
            var stateLayer;
            var stateBoundry;
            var poupdata;
            var mrrdata;
            var statesColor = new Color('#4E79BE');


            //tokens in newDashboard for local systems

            var origin = window.location.origin;

            stateLayer = new FeatureLayer(serviceUrl + '/0' + serviceUrlToken, {
                mode: FeatureLayer.MODE_ONDEMAND,
                outFields: ["STNAME", "STCODE11"],
                displayField: "STNAME",
                showLabels: true,
            });
            var defaultSymbol = new SimpleFillSymbol().setStyle(SimpleFillSymbol.STYLE_NULL);
            defaultSymbol.outline.setStyle(SimpleLineSymbol.STYLE_NULL);
            var renderer = new UniqueValueRenderer(defaultSymbol, "State_LGD");
            map.on("load", function () {
                map.disableScrollWheelZoom();
                map.disableDoubleClickZoom();
                map.disableMapNavigation();
                map.disableRubberBandZoom();
                map.disablePan();
                map.disableKeyboardNavigation();
                map.disableShiftDoubleClickZoom();

            });
            map.on("click", function (evt) {
                return false;
            });
            $.ajax({
                type: 'POST',
                url: misUrl + 'category=16',
                dataType: 'json',
                async: false,
                success: function (data) {
                    clrdata = data;
                    for (var i = 0; i < data.length; i++) {
                        var scode = data[i].lgd.toString();
                        var j = data[i].percent.toString();
                        const selectedColor = fillColorOnMap(j);
                        renderer.addValue(scode, new SimpleFillSymbol(
                            "solid",
                            new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color(statesColor), 0.7),
                            new Color(selectedColor)
                        ));
                    }
                }
            });
            stateLayer.on("mouse-over", showTooltip);
            stateLayer.on("mouse-out", closeDialog);
            var highlightSymbol = new SimpleFillSymbol(
                SimpleFillSymbol.STYLE_SOLID,
                new SimpleLineSymbol(
                    SimpleLineSymbol.STYLE_SOLID,
                    new Color("#008000"), 3
                ),
                new Color([125, 125, 125, 0.35])
            );
            function showTooltip(evt) {
                closeDialog();
                var total, completed, percentt;
                var stname = evt.graphic.attributes.STNAME;
                for (var i = 0; i < clrdata.length; i++) {
                    var dcode = clrdata[i].lgd.toString();
                    if (dcode == evt.graphic.attributes.State_LGD) {
                        total = parseInt(clrdata[i].total.toString());
                        completed = parseInt(clrdata[i].completed.toString());
                        if (completed == null || completed == 0) {
                            percentt = '0';
                        } else {
                            percentt = (completed / total) * 100;
                            percentt = percentt.toFixed(2);
                        }

                        break;
                    }
                }
                var highlightGraphic = new Graphic(evt.graphic.geometry, highlightSymbol);
                map.graphics.add(highlightGraphic);
                var table = "";
                table = '<div>' + 'StateName' + ' : <font color="orange">' + stname + '</font><br>' + 'Total (Tehsils)' + ' : <font color="orange">' + total + '</font><br>' + 'Completed (Tehsils)' + ': <font color="orange">'
                    + completed + '</font><br>' + 'Percentage %' + ' : <font color="orange">' + percentt + '</font></div>';
                map.infoWindow.setTitle("Modern Record Room");
                map.infoWindow.setContent(table);
                map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
            }
            function closeDialog() {
                map.graphics.clear();
                var widget = dijit.byId("tooltipDialog");
                if (widget) {
                    widget.destroy();
                }
            }
            stateLayer.redraw();
            stateLayer.setRenderer(renderer);
            map.addLayers([stateLayer]);
            registerStateLayer(map, stateLayer, hpGen);
        });
    }
    else if (cat === "40") {
        var map;
        var mapId = "mapDiv";
        require([
            "dojo/dom-construct", "esri/dijit/BasemapGallery", "esri/dijit/BasemapToggle", "dijit/dijit",
            "esri/map", "esri/geometry/webMercatorUtils",
            "esri/layers/FeatureLayer",
            "esri/dijit/Legend", "esri/dijit/Search", "esri/renderers/ScaleDependentRenderer",
            "esri/symbols/Font", "esri/geometry/Point",
            "esri/SpatialReference", "esri/graphic", "esri/lang",
            "esri/dijit/PopupTemplate", "dijit/Dialog", "dijit/TooltipDialog",
            "esri/renderers/UniqueValueRenderer", "esri/symbols/TextSymbol",
            "dijit/registry", "dijit/form/Button", "dijit/TooltipDialog",
            "dijit/popup", "esri/arcgis/utils",
            "esri/geometry/Extent",
            "esri/InfoTemplate", "esri/symbols/SimpleMarkerSymbol",
            "esri/symbols/PictureMarkerSymbol", "esri/symbols/SimpleLineSymbol",
            "esri/symbols/SimpleFillSymbol", "esri/renderers/SimpleRenderer",
            "esri/Color", "dojo/number", "dojo/dom-style",
            "dojo/domReady!"
        ], function (
            domConstruct, BasemapGallery, BasemapToggle, dijit,
            Map, webMercatorUtils, FeatureLayer, Legend,
            Search, ScaleDependentRenderer, Font, Point, SpatialReference,
            Graphic, esriLang, PopupTemplate, Dialog, TooltipDialog,
            UniqueValueRenderer, TextSymbol, registry,
            Button, TooltipDialog, popup,
            utils,
            Extent,
            InfoTemplate, SimpleMarkerSymbol, PictureMarkerSymbol,
            SimpleLineSymbol, SimpleFillSymbol,
            SimpleRenderer, Color, number, Style
        ) {

            var bounds = new Extent({
                "xmin": 7189291.79,
                "ymin": 563260.13,
                "xmax": 11244335.73,
                "ymax": 4780505.83,
                "spatialReference": { "wkid": 102100 }
            });
            var map = new Map(mapId, {
                extent: bounds,
                zoom: 5,
                slider: false,
                "maxScale": 500, //0
                "minScale": 56000000, // 100000000/56000000
                showLabels: true
            });
            var stateLayer;
            var stateBoundry;
            var poupdata;
            var mrrdata;
            var statesColor = new Color('#4E79BE');


            //tokens in newDashboard for local systems

            var origin = window.location.origin;

            stateLayer = new FeatureLayer(serviceUrl + '/0' + serviceUrlToken, {
                mode: FeatureLayer.MODE_ONDEMAND,
                outFields: ["STNAME", "STCODE11"],
                displayField: "STNAME",
                showLabels: true,
            });
            var defaultSymbol = new SimpleFillSymbol().setStyle(SimpleFillSymbol.STYLE_NULL);
            defaultSymbol.outline.setStyle(SimpleLineSymbol.STYLE_NULL);
            var renderer = new UniqueValueRenderer(defaultSymbol, "State_LGD");
            map.on("load", function () {
                map.disableScrollWheelZoom();
                map.disableDoubleClickZoom();
                map.disableMapNavigation();
                map.disableRubberBandZoom();
                map.disablePan();
                map.disableKeyboardNavigation();
                map.disableShiftDoubleClickZoom();

            });
            map.on("click", function (evt) {
                return false;
            });
            $.ajax({
                type: 'POST',
                url: misUrl + 'category=40',
                dataType: 'json',
                async: false,
                success: function (data) {
                    clrdata = data;
                    for (var i = 0; i < data.length; i++) {
                        var scode = data[i].lgd.toString();
                        var j = data[i].percent.toString();
                        const selectedColor = fillColorOnMap(j);
                        renderer.addValue(scode, new SimpleFillSymbol(
                            "solid",
                            new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color(statesColor), 0.7),
                            new Color(selectedColor)
                        ));
                    }
                }
            });
            stateLayer.on("mouse-over", showTooltip);
            stateLayer.on("mouse-out", closeDialog);
            var highlightSymbol = new SimpleFillSymbol(
                SimpleFillSymbol.STYLE_SOLID,
                new SimpleLineSymbol(
                    SimpleLineSymbol.STYLE_SOLID,
                    new Color("#008000"), 3
                ),
                new Color([125, 125, 125, 0.35])
            );
            function showTooltip(evt) {
                closeDialog();
                var total, completed, percentt;
                var stname = evt.graphic.attributes.STNAME;
                for (var i = 0; i < clrdata.length; i++) {
                    var dcode = clrdata[i].lgd.toString();
                    if (dcode == evt.graphic.attributes.State_LGD) {
                        total = parseInt(clrdata[i].total.toString());
                        completed = parseInt(clrdata[i].completed.toString());
                        if (completed == null || completed == 0) {
                            percentt = '0';
                        } else {
                            percentt = (completed / total) * 100;
                            percentt = percentt.toFixed(2);
                        }
                        break;
                    }
                }
                var highlightGraphic = new Graphic(evt.graphic.geometry, highlightSymbol);
                map.graphics.add(highlightGraphic);
                var table = "";
                table = '<div>' + 'StateName' + ' : <font color="orange">' + stname + '</font><br>' + 'Total (Villages)' + ' : <font color="orange">' + total + '</font><br>' + 'Completed Survey (Villages)' + ': <font color="orange">'
                    + completed + '</font><br>' + 'Percentage %' + ' : <font color="orange">' + percentt + '</font></div>';
                map.infoWindow.setTitle("Survey Reservey Status");
                map.infoWindow.setContent(table);
                map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
            }
            function closeDialog() {
                map.graphics.clear();
                var widget = dijit.byId("tooltipDialog");
                if (widget) {
                    widget.destroy();
                }
            }
            stateLayer.redraw();
            stateLayer.setRenderer(renderer);
            map.addLayers([stateLayer]);
            registerStateLayer(map, stateLayer, hpGen);
        });
    }
    else if (cat === "50") {
        var map;
        var mapId = "mapDiv";
        require([
            "dojo/dom-construct", "esri/dijit/BasemapGallery", "esri/dijit/BasemapToggle", "dijit/dijit",
            "esri/map", "esri/geometry/webMercatorUtils",
            "esri/layers/FeatureLayer",
            "esri/dijit/Legend", "esri/dijit/Search", "esri/renderers/ScaleDependentRenderer",
            "esri/symbols/Font", "esri/geometry/Point",
            "esri/SpatialReference", "esri/graphic", "esri/lang",
            "esri/dijit/PopupTemplate", "dijit/Dialog", "dijit/TooltipDialog",
            "esri/renderers/UniqueValueRenderer", "esri/symbols/TextSymbol",
            "dijit/registry", "dijit/form/Button", "dijit/TooltipDialog",
            "dijit/popup", "esri/arcgis/utils",
            "esri/geometry/Extent",
            "esri/InfoTemplate", "esri/symbols/SimpleMarkerSymbol",
            "esri/symbols/PictureMarkerSymbol", "esri/symbols/SimpleLineSymbol",
            "esri/symbols/SimpleFillSymbol", "esri/renderers/SimpleRenderer",
            "esri/Color", "dojo/number", "dojo/dom-style",
            "dojo/domReady!"
        ], function (
            domConstruct, BasemapGallery, BasemapToggle, dijit,
            Map, webMercatorUtils, FeatureLayer, Legend,
            Search, ScaleDependentRenderer, Font, Point, SpatialReference,
            Graphic, esriLang, PopupTemplate, Dialog, TooltipDialog,
            UniqueValueRenderer, TextSymbol, registry,
            Button, TooltipDialog, popup,
            utils,
            Extent,
            InfoTemplate, SimpleMarkerSymbol, PictureMarkerSymbol,
            SimpleLineSymbol, SimpleFillSymbol,
            SimpleRenderer, Color, number, Style
        ) {

            var bounds = new Extent({
                "xmin": 7189291.79,
                "ymin": 563260.13,
                "xmax": 11244335.73,
                "ymax": 4780505.83,
                "spatialReference": { "wkid": 102100 }
            });
            var map = new Map(mapId, {
                extent: bounds,
                zoom: 5,
                slider: false,
                "maxScale": 500, //0
                "minScale": 56000000, // 100000000/56000000
                showLabels: true
            });
            var stateLayer;
            var stateBoundry;
            var poupdata;
            var mrrdata;
            var statesColor = new Color('#4E79BE');


            //tokens in newDashboard for local systems

            var origin = window.location.origin;

            stateLayer = new FeatureLayer(serviceUrl + '/0' + serviceUrlToken, {
                mode: FeatureLayer.MODE_ONDEMAND,
                outFields: ["STNAME", "STCODE11"],
                displayField: "STNAME",
                showLabels: true,
            });
            var defaultSymbol = new SimpleFillSymbol().setStyle(SimpleFillSymbol.STYLE_NULL);
            defaultSymbol.outline.setStyle(SimpleLineSymbol.STYLE_NULL);
            var renderer = new UniqueValueRenderer(defaultSymbol, "State_LGD");
            map.on("load", function () {
                map.disableScrollWheelZoom();
                map.disableDoubleClickZoom();
                map.disableMapNavigation();
                map.disableRubberBandZoom();
                map.disablePan();
                map.disableKeyboardNavigation();
                map.disableShiftDoubleClickZoom();

            });
            map.on("click", function (evt) {
                return false;
            });
            $.ajax({
                type: 'POST',
                url: misUrl + 'category=50',
                dataType: 'json',
                async: false,
                success: function (data) {
                    clrdata = data;
                    for (var i = 0; i < data.length; i++) {
                        var scode = data[i].lgd.toString();
                        var j = data[i].percent.toString();
                        const selectedColor = fillColorOnMap(j);
                        renderer.addValue(scode, new SimpleFillSymbol(
                            "solid",
                            new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color(statesColor), 0.7),
                            new Color(selectedColor)
                        ));
                    }
                }
            });
            stateLayer.on("mouse-over", showTooltip);
            stateLayer.on("mouse-out", closeDialog);
            var highlightSymbol = new SimpleFillSymbol(
                SimpleFillSymbol.STYLE_SOLID,
                new SimpleLineSymbol(
                    SimpleLineSymbol.STYLE_SOLID,
                    new Color("#008000"), 3
                ),
                new Color([125, 125, 125, 0.35])
            );
            function showTooltip(evt) {
                closeDialog();
                var total, completed, percentt;
                var stname = evt.graphic.attributes.STNAME;
                for (var i = 0; i < clrdata.length; i++) {
                    var dcode = clrdata[i].lgd.toString();
                    if (dcode == evt.graphic.attributes.State_LGD) {
                        total = parseInt(clrdata[i].total.toString());
                        completed = parseInt(clrdata[i].completed.toString());
                        if (completed == null || completed == 0) {
                            percentt = '0';
                        } else {
                            percentt = (completed / total) * 100;
                            percentt = percentt.toFixed(2);
                        }
                        break;
                    }
                }
                var highlightGraphic = new Graphic(evt.graphic.geometry, highlightSymbol);
                map.graphics.add(highlightGraphic);
                var table = "";
                table = '<div>' + 'StateName' + ' : <font color="orange">' + stname + '</font><br>' + 'Total (SRO)' + ' : <font color="orange">' + total + '</font><br>' + 'Computerized(SRO)' + ': <font color="orange">'
                    + completed + '</font><br>' + 'Percentage %' + ' : <font color="orange">' + percentt + '</font></div>';
                map.infoWindow.setTitle("SRO Status");
                map.infoWindow.setContent(table);
                map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
            }
            function closeDialog() {
                map.graphics.clear();
                var widget = dijit.byId("tooltipDialog");
                if (widget) {
                    widget.destroy();
                }
            }
            stateLayer.redraw();
            stateLayer.setRenderer(renderer);
            map.addLayers([stateLayer]);
            registerStateLayer(map, stateLayer, hpGen);
        });
    }
    else if (cat === "60") {
        var map;
        var mapId = "mapDiv";
        require([
            "dojo/dom-construct", "esri/dijit/BasemapGallery", "esri/dijit/BasemapToggle", "dijit/dijit",
            "esri/map", "esri/geometry/webMercatorUtils",
            "esri/layers/FeatureLayer",
            "esri/dijit/Legend", "esri/dijit/Search", "esri/renderers/ScaleDependentRenderer",
            "esri/symbols/Font", "esri/geometry/Point",
            "esri/SpatialReference", "esri/graphic", "esri/lang",
            "esri/dijit/PopupTemplate", "dijit/Dialog", "dijit/TooltipDialog",
            "esri/renderers/UniqueValueRenderer", "esri/symbols/TextSymbol",
            "dijit/registry", "dijit/form/Button", "dijit/TooltipDialog",
            "dijit/popup", "esri/arcgis/utils",
            "esri/geometry/Extent",
            "esri/InfoTemplate", "esri/symbols/SimpleMarkerSymbol",
            "esri/symbols/PictureMarkerSymbol", "esri/symbols/SimpleLineSymbol",
            "esri/symbols/SimpleFillSymbol", "esri/renderers/SimpleRenderer",
            "esri/Color", "dojo/number", "dojo/dom-style",
            "dojo/domReady!"
        ], function (
            domConstruct, BasemapGallery, BasemapToggle, dijit,
            Map, webMercatorUtils, FeatureLayer, Legend,
            Search, ScaleDependentRenderer, Font, Point, SpatialReference,
            Graphic, esriLang, PopupTemplate, Dialog, TooltipDialog,
            UniqueValueRenderer, TextSymbol, registry,
            Button, TooltipDialog, popup,
            utils,
            Extent,
            InfoTemplate, SimpleMarkerSymbol, PictureMarkerSymbol,
            SimpleLineSymbol, SimpleFillSymbol,
            SimpleRenderer, Color, number, Style
        ) {

            var bounds = new Extent({
                "xmin": 7189291.79,
                "ymin": 563260.13,
                "xmax": 11244335.73,
                "ymax": 4780505.83,
                "spatialReference": { "wkid": 102100 }
            });
            var map = new Map(mapId, {
                extent: bounds,
                zoom: 5,
                slider: false,
                "maxScale": 500, //0
                "minScale": 56000000, // 100000000/56000000
                showLabels: true
            });
            var stateLayer;
            var stateBoundry;
            var poupdata;
            var mrrdata;
            var statesColor = new Color('#4E79BE');


            //tokens in newDashboard for local systems

            var origin = window.location.origin;

            stateLayer = new FeatureLayer(serviceUrl + '/0' + serviceUrlToken, {
                mode: FeatureLayer.MODE_ONDEMAND,
                outFields: ["STNAME", "STCODE11"],
                displayField: "STNAME",
                showLabels: true,
            });
            var defaultSymbol = new SimpleFillSymbol().setStyle(SimpleFillSymbol.STYLE_NULL);
            defaultSymbol.outline.setStyle(SimpleLineSymbol.STYLE_NULL);
            var renderer = new UniqueValueRenderer(defaultSymbol, "State_LGD");
            map.on("load", function () {
                map.disableScrollWheelZoom();
                map.disableDoubleClickZoom();
                map.disableMapNavigation();
                map.disableRubberBandZoom();
                map.disablePan();
                map.disableKeyboardNavigation();
                map.disableShiftDoubleClickZoom();

            });
            map.on("click", function (evt) {
                return false;
            });
            $.ajax({
                type: 'POST',
                url: misUrl + 'category=60',
                dataType: 'json',
                async: false,
                success: function (data) {
                    clrdata = data;
                    for (var i = 0; i < data.length; i++) {
                        var scode = data[i].lgd.toString();
                        var j = data[i].percent.toString();
                        const selectedColor = fillColorOnMap(j);
                        renderer.addValue(scode, new SimpleFillSymbol(
                            "solid",
                            new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color(statesColor), 0.7),
                            new Color(selectedColor)
                        ));
                    }
                }
            });
            stateLayer.on("mouse-over", showTooltip);
            stateLayer.on("mouse-out", closeDialog);
            var highlightSymbol = new SimpleFillSymbol(
                SimpleFillSymbol.STYLE_SOLID,
                new SimpleLineSymbol(
                    SimpleLineSymbol.STYLE_SOLID,
                    new Color("#008000"), 3
                ),
                new Color([125, 125, 125, 0.35])
            );
            function showTooltip(evt) {
                closeDialog();
                var total, completed, percentt;
                var stname = evt.graphic.attributes.STNAME;
                for (var i = 0; i < clrdata.length; i++) {
                    var dcode = clrdata[i].lgd.toString();
                    if (dcode == evt.graphic.attributes.State_LGD) {
                        total = parseInt(clrdata[i].total.toString());
                        completed = parseInt(clrdata[i].completed.toString());
                        if (completed == null || completed == 0) {
                            percentt = '0';
                        } else {
                            percentt = (completed / total) * 100;
                            percentt = percentt.toFixed(2);
                        }
                        break;
                    }
                }
                var highlightGraphic = new Graphic(evt.graphic.geometry, highlightSymbol);
                map.graphics.add(highlightGraphic);
                var table = "";
                table = '<div>' + 'StateName' + ' : <font color="orange">' + stname + '</font><br>' + 'Total (SRO)' + ' : <font color="orange">' + total + '</font><br>' + 'SRO Integrated with LR' + ': <font color="orange">'
                    + completed + '</font><br>' + 'Percentage %' + ' : <font color="orange">' + percentt + '</font></div>';
                map.infoWindow.setTitle("SRO Status");
                map.infoWindow.setContent(table);
                map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
            }
            function closeDialog() {
                map.graphics.clear();
                var widget = dijit.byId("tooltipDialog");
                if (widget) {
                    widget.destroy();
                }
            }
            stateLayer.redraw();
            stateLayer.setRenderer(renderer);
            map.addLayers([stateLayer]);
            registerStateLayer(map, stateLayer, hpGen);
        });
    }
    else if (cat === "70") {

        var map;
        var mapId = "mapDiv";
        require([
            "dojo/dom-construct", "esri/dijit/BasemapGallery", "esri/dijit/BasemapToggle", "dijit/dijit",
            "esri/map", "esri/geometry/webMercatorUtils",
            "esri/layers/FeatureLayer",
            "esri/dijit/Legend", "esri/dijit/Search", "esri/renderers/ScaleDependentRenderer",
            "esri/symbols/Font", "esri/geometry/Point",
            "esri/SpatialReference", "esri/graphic", "esri/lang",
            "esri/dijit/PopupTemplate", "dijit/Dialog", "dijit/TooltipDialog",
            "esri/renderers/UniqueValueRenderer", "esri/symbols/TextSymbol",
            "dijit/registry", "dijit/form/Button", "dijit/TooltipDialog",
            "dijit/popup", "esri/arcgis/utils",
            "esri/geometry/Extent",
            "esri/InfoTemplate", "esri/symbols/SimpleMarkerSymbol",
            "esri/symbols/PictureMarkerSymbol", "esri/symbols/SimpleLineSymbol",
            "esri/symbols/SimpleFillSymbol", "esri/renderers/SimpleRenderer",
            "esri/Color", "dojo/number", "dojo/dom-style",
            "dojo/domReady!"
        ], function (
            domConstruct, BasemapGallery, BasemapToggle, dijit,
            Map, webMercatorUtils, FeatureLayer, Legend,
            Search, ScaleDependentRenderer, Font, Point, SpatialReference,
            Graphic, esriLang, PopupTemplate, Dialog, TooltipDialog,
            UniqueValueRenderer, TextSymbol, registry,
            Button, TooltipDialog, popup,
            utils,
            Extent,
            InfoTemplate, SimpleMarkerSymbol, PictureMarkerSymbol,
            SimpleLineSymbol, SimpleFillSymbol,
            SimpleRenderer, Color, number, Style
        ) {

            var bounds = new Extent({
                "xmin": 7189291.79,
                "ymin": 563260.13,
                "xmax": 11244335.73,
                "ymax": 4780505.83,
                "spatialReference": { "wkid": 102100 }
            });
            var map = new Map(mapId, {
                extent: bounds,
                zoom: 5,
                slider: false,
                "maxScale": 500, //0
                "minScale": 56000000, // 100000000/56000000
                showLabels: true
            });
            var stateLayer;
            var stateBoundry;
            var poupdata;
            var mrrdata;
            var statesColor = new Color('#4E79BE');



            //tokens in newDashboard for local systems

            var origin = window.location.origin;

            stateLayer = new FeatureLayer(serviceUrl + '/0' + serviceUrlToken, {
                mode: FeatureLayer.MODE_ONDEMAND,
                outFields: ["STNAME", "STCODE11"],
                displayField: "STNAME",
                showLabels: true,
            });
            var defaultSymbol = new SimpleFillSymbol().setStyle(SimpleFillSymbol.STYLE_NULL);
            defaultSymbol.outline.setStyle(SimpleLineSymbol.STYLE_NULL);
            var renderer = new UniqueValueRenderer(defaultSymbol, "State_LGD");
            map.on("load", function () {
                map.disableScrollWheelZoom();
                map.disableDoubleClickZoom();
                map.disableMapNavigation();
                map.disableRubberBandZoom();
                map.disablePan();
                map.disableKeyboardNavigation();
                map.disableShiftDoubleClickZoom();

            });
            map.on("click", function (evt) {
                return false;
            });
            $.ajax({
                type: 'POST',
                url: misUrl + 'category=70',
                dataType: 'json',
                async: false,
                success: function (data) {
                    clrdata = data;
                    for (var i = 0; i < data.length; i++) {
                        var scode = data[i].lgd.toString();
                        var j = data[i].percent.toString();
                        const selectedColor = fillColorOnMap(j);
                        renderer.addValue(scode, new SimpleFillSymbol(
                            "solid",
                            new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color(statesColor), 0.7),
                            new Color(selectedColor)
                        ));
                    }
                }
            });
            stateLayer.on("mouse-over", showTooltip);
            stateLayer.on("mouse-out", closeDialog);
            var highlightSymbol = new SimpleFillSymbol(
                SimpleFillSymbol.STYLE_SOLID,
                new SimpleLineSymbol(
                    SimpleLineSymbol.STYLE_SOLID,
                    new Color("#008000"), 3
                ),
                new Color([125, 125, 125, 0.35])
            );
            function showTooltip(evt) {
                closeDialog();
                var totalvillages, villageswithcadstralmaplinkedtoror, percentt;
                var stname = evt.graphic.attributes.STNAME;
                for (var i = 0; i < clrdata.length; i++) {
                    var scode = clrdata[i].lgd.toString();
                    if (scode == evt.graphic.attributes.State_LGD) {
                        totalvillages = parseInt(clrdata[i].totalvillages.toString());
                        villageswithcadstralmaplinkedtoror = parseInt(clrdata[i].linkedtoror.toString());
                        if (villageswithcadstralmaplinkedtoror == null || villageswithcadstralmaplinkedtoror == 0) {
                            percentt = '0';
                        } else {
                            percentt = (villageswithcadstralmaplinkedtoror / totalvillages) * 100;
                            percentt = percentt.toFixed(2);
                        }


                        break;
                    }
                }
                var highlightGraphic = new Graphic(evt.graphic.geometry, highlightSymbol);
                map.graphics.add(highlightGraphic);
                var table = "";
                table = '<div>' + 'StateName' + ' : <font color="orange">' + stname + '</font><br>' + 'Total (Villages)' + ' : <font color="orange">' + totalvillages + '</font><br>' + 'Cadastral Map linked to RoR (Villages)' + ': <font color="orange">'
                    + villageswithcadstralmaplinkedtoror + '</font><br>' + 'Percentage %' + ' : <font color="orange">' + percentt + '</font></div>';
                map.infoWindow.setTitle("Cadastral Map linked to RoR (Villages)");
                map.infoWindow.setContent(table);
                map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
            }
            function closeDialog() {
                map.graphics.clear();
                var widget = dijit.byId("tooltipDialog");
                if (widget) {
                    widget.destroy();
                }
            }
            stateLayer.redraw();
            stateLayer.setRenderer(renderer);
            map.addLayers([stateLayer]);
            registerStateLayer(map, stateLayer, hpGen);
        });

    }
    else if (cat === "80") {

        var map;
        var mapId = "mapDiv";
        require([
            "dojo/dom-construct", "esri/dijit/BasemapGallery", "esri/dijit/BasemapToggle", "dijit/dijit",
            "esri/map", "esri/geometry/webMercatorUtils",
            "esri/layers/FeatureLayer",
            "esri/dijit/Legend", "esri/dijit/Search", "esri/renderers/ScaleDependentRenderer",
            "esri/symbols/Font", "esri/geometry/Point",
            "esri/SpatialReference", "esri/graphic", "esri/lang",
            "esri/dijit/PopupTemplate", "dijit/Dialog", "dijit/TooltipDialog",
            "esri/renderers/UniqueValueRenderer", "esri/symbols/TextSymbol",
            "dijit/registry", "dijit/form/Button", "dijit/TooltipDialog",
            "dijit/popup", "esri/arcgis/utils",
            "esri/geometry/Extent",
            "esri/InfoTemplate", "esri/symbols/SimpleMarkerSymbol",
            "esri/symbols/PictureMarkerSymbol", "esri/symbols/SimpleLineSymbol",
            "esri/symbols/SimpleFillSymbol", "esri/renderers/SimpleRenderer",
            "esri/Color", "dojo/number", "dojo/dom-style",
            "dojo/domReady!"
        ], function (
            domConstruct, BasemapGallery, BasemapToggle, dijit,
            Map, webMercatorUtils, FeatureLayer, Legend,
            Search, ScaleDependentRenderer, Font, Point, SpatialReference,
            Graphic, esriLang, PopupTemplate, Dialog, TooltipDialog,
            UniqueValueRenderer, TextSymbol, registry,
            Button, TooltipDialog, popup,
            utils,
            Extent,
            InfoTemplate, SimpleMarkerSymbol, PictureMarkerSymbol,
            SimpleLineSymbol, SimpleFillSymbol,
            SimpleRenderer, Color, number, Style
        ) {

            var bounds = new Extent({
                "xmin": 7189291.79,
                "ymin": 563260.13,
                "xmax": 11244335.73,
                "ymax": 4780505.83,
                "spatialReference": { "wkid": 102100 }
            });
            var map = new Map(mapId, {
                extent: bounds,
                zoom: 5,
                slider: false,
                "maxScale": 500, //0
                "minScale": 56000000, // 100000000/56000000
                showLabels: true
            });
            var stateLayer;
            var stateBoundry;
            var poupdata;
            var mrrdata;
            var statesColor = new Color('#4E79BE');



            //tokens in newDashboard for local systems

            var origin = window.location.origin;

            stateLayer = new FeatureLayer(serviceUrl + '/0' + serviceUrlToken, {
                mode: FeatureLayer.MODE_ONDEMAND,
                outFields: ["STNAME", "STCODE11"],
                displayField: "STNAME",
                showLabels: true,
            });
            var defaultSymbol = new SimpleFillSymbol().setStyle(SimpleFillSymbol.STYLE_NULL);
            defaultSymbol.outline.setStyle(SimpleLineSymbol.STYLE_NULL);
            var renderer = new UniqueValueRenderer(defaultSymbol, "State_LGD");
            map.on("load", function () {
                map.disableScrollWheelZoom();
                map.disableDoubleClickZoom();
                map.disableMapNavigation();
                map.disableRubberBandZoom();
                map.disablePan();
                map.disableKeyboardNavigation();
                map.disableShiftDoubleClickZoom();

            });
            map.on("click", function (evt) {
                return false;
            });
            $.ajax({
                type: 'POST',
                url: misUrl + 'category=80',
                dataType: 'json',
                async: false,
                success: function (data) {
                    clrdata = data;
                    for (var i = 0; i < data.length; i++) {
                        var scode = data[i].lgd.toString();
                        var j = data[i].percent.toString();
                        const selectedColor = fillColorOnMap(j);
                        renderer.addValue(scode, new SimpleFillSymbol(
                            "solid",
                            new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color(statesColor), 0.7),
                            new Color(selectedColor)
                        ));
                    }
                }
            });
            stateLayer.on("mouse-over", showTooltip);
            stateLayer.on("mouse-out", closeDialog);
            var highlightSymbol = new SimpleFillSymbol(
                SimpleFillSymbol.STYLE_SOLID,
                new SimpleLineSymbol(
                    SimpleLineSymbol.STYLE_SOLID,
                    new Color("#008000"), 3
                ),
                new Color([125, 125, 125, 0.35])
            );
            function showTooltip(evt) {
                closeDialog();
                var totalvillages, villageswithcadstralmaplinkedtoror, percentt;
                var stname = evt.graphic.attributes.STNAME;
                for (var i = 0; i < clrdata.length; i++) {
                    var scode = clrdata[i].lgd.toString();
                    if (scode == evt.graphic.attributes.State_LGD) {
                        totalvillages = parseInt(clrdata[i].total.toString());
                        villageswithcadstralmaplinkedtoror = parseInt(clrdata[i].completed.toString());
                        if (villageswithcadstralmaplinkedtoror == null || villageswithcadstralmaplinkedtoror == 0) {
                            percentt = '0';
                        } else {
                            percentt = (villageswithcadstralmaplinkedtoror / totalvillages) * 100;
                            percentt = percentt.toFixed(2);
                        }


                        break;
                    }
                }
                var highlightGraphic = new Graphic(evt.graphic.geometry, highlightSymbol);
                map.graphics.add(highlightGraphic);
                var table = "";
                table = '<div>' + 'StateName' + ' : <font color="orange">' + stname + '</font><br>' + 'Total Number of Revenue Courts' + ' : <font color="orange">' + totalvillages + '</font><br>' + 'Total Number of Computerized Revenue Courts' + ': <font color="orange">'
                    + villageswithcadstralmaplinkedtoror + '</font><br>' + 'Percentage %' + ' : <font color="orange">' + percentt + '</font></div>';
                map.infoWindow.setTitle("Revenue Court Status");
                map.infoWindow.setContent(table);
                map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
            }
            function closeDialog() {
                map.graphics.clear();
                var widget = dijit.byId("tooltipDialog");
                if (widget) {
                    widget.destroy();
                }
            }
            stateLayer.redraw();
            stateLayer.setRenderer(renderer);
            map.addLayers([stateLayer]);
            registerStateLayer(map, stateLayer, hpGen);
        });

    }
    else if (cat === "90") {

        var map;
        var mapId = "mapDiv";
        require([
            "dojo/dom-construct", "esri/dijit/BasemapGallery", "esri/dijit/BasemapToggle", "dijit/dijit",
            "esri/map", "esri/geometry/webMercatorUtils",
            "esri/layers/FeatureLayer",
            "esri/dijit/Legend", "esri/dijit/Search", "esri/renderers/ScaleDependentRenderer",
            "esri/symbols/Font", "esri/geometry/Point",
            "esri/SpatialReference", "esri/graphic", "esri/lang",
            "esri/dijit/PopupTemplate", "dijit/Dialog", "dijit/TooltipDialog",
            "esri/renderers/UniqueValueRenderer", "esri/symbols/TextSymbol",
            "dijit/registry", "dijit/form/Button", "dijit/TooltipDialog",
            "dijit/popup", "esri/arcgis/utils",
            "esri/geometry/Extent",
            "esri/InfoTemplate", "esri/symbols/SimpleMarkerSymbol",
            "esri/symbols/PictureMarkerSymbol", "esri/symbols/SimpleLineSymbol",
            "esri/symbols/SimpleFillSymbol", "esri/renderers/SimpleRenderer",
            "esri/Color", "dojo/number", "dojo/dom-style",
            "dojo/domReady!"
        ], function (
            domConstruct, BasemapGallery, BasemapToggle, dijit,
            Map, webMercatorUtils, FeatureLayer, Legend,
            Search, ScaleDependentRenderer, Font, Point, SpatialReference,
            Graphic, esriLang, PopupTemplate, Dialog, TooltipDialog,
            UniqueValueRenderer, TextSymbol, registry,
            Button, TooltipDialog, popup,
            utils,
            Extent,
            InfoTemplate, SimpleMarkerSymbol, PictureMarkerSymbol,
            SimpleLineSymbol, SimpleFillSymbol,
            SimpleRenderer, Color, number, Style
        ) {

            var bounds = new Extent({
                "xmin": 7189291.79,
                "ymin": 563260.13,
                "xmax": 11244335.73,
                "ymax": 4780505.83,
                "spatialReference": { "wkid": 102100 }
            });
            var map = new Map(mapId, {
                extent: bounds,
                zoom: 5,
                slider: false,
                "maxScale": 500, //0
                "minScale": 56000000, // 100000000/56000000
                showLabels: true
            });
            var stateLayer;
            var stateBoundry;
            var poupdata;
            var mrrdata;
            var statesColor = new Color('#4E79BE');



            //tokens in newDashboard for local systems

            var origin = window.location.origin;

            stateLayer = new FeatureLayer(serviceUrl + '/0' + serviceUrlToken, {
                mode: FeatureLayer.MODE_ONDEMAND,
                outFields: ["STNAME", "STCODE11"],
                displayField: "STNAME",
                showLabels: true,
            });
            var defaultSymbol = new SimpleFillSymbol().setStyle(SimpleFillSymbol.STYLE_NULL);
            defaultSymbol.outline.setStyle(SimpleLineSymbol.STYLE_NULL);
            var renderer = new UniqueValueRenderer(defaultSymbol, "State_LGD");
            map.on("load", function () {
                map.disableScrollWheelZoom();
                map.disableDoubleClickZoom();
                map.disableMapNavigation();
                map.disableRubberBandZoom();
                map.disablePan();
                map.disableKeyboardNavigation();
                map.disableShiftDoubleClickZoom();

            });
            map.on("click", function (evt) {
                return false;
            });
            $.ajax({
                type: 'POST',
                url: misUrl + 'category=90',
                dataType: 'json',
                async: false,
                success: function (data) {
                    clrdata = data;
                    for (var i = 0; i < data.length; i++) {
                        var scode = data[i].lgd.toString();
                        var j = data[i].percent.toString();
                        const selectedColor = fillColorOnMap(j);
                        renderer.addValue(scode, new SimpleFillSymbol(
                            "solid",
                            new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color(statesColor), 0.7),
                            new Color(selectedColor)
                        ));
                    }
                }
            });
            stateLayer.on("mouse-over", showTooltip);
            stateLayer.on("mouse-out", closeDialog);
            var highlightSymbol = new SimpleFillSymbol(
                SimpleFillSymbol.STYLE_SOLID,
                new SimpleLineSymbol(
                    SimpleLineSymbol.STYLE_SOLID,
                    new Color("#008000"), 3
                ),
                new Color([125, 125, 125, 0.35])
            );
            function showTooltip(evt) {
                closeDialog();
                var totalvillages, villageswithcadstralmaplinkedtoror, percentt;
                var stname = evt.graphic.attributes.STNAME;
                for (var i = 0; i < clrdata.length; i++) {
                    var scode = clrdata[i].lgd.toString();
                    if (scode == evt.graphic.attributes.State_LGD) {
                        totalvillages = parseInt(clrdata[i].total.toString());
                        villageswithcadstralmaplinkedtoror = parseInt(clrdata[i].completed.toString());
                        if (villageswithcadstralmaplinkedtoror == null || villageswithcadstralmaplinkedtoror == 0) {
                            percentt = '0';
                        } else {
                            percentt = (villageswithcadstralmaplinkedtoror / totalvillages) * 100;
                            percentt = percentt.toFixed(2);
                        }


                        break;
                    }
                }
                var highlightGraphic = new Graphic(evt.graphic.geometry, highlightSymbol);
                map.graphics.add(highlightGraphic);
                var table = "";
                table = '<div>' + 'StateName' + ' : <font color="orange">' + stname + '</font><br>' + 'Total Number of Villages' + ' : <font color="orange">' + totalvillages + '</font><br>' + 'Where 100% RoR linked with Aadhaar' + ': <font color="orange">'
                    + villageswithcadstralmaplinkedtoror + '</font><br>' + 'Percentage %' + ' : <font color="orange">' + percentt + '</font></div>';
                map.infoWindow.setTitle("Aadhar Linkage Status");
                map.infoWindow.setContent(table);
                map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
            }
            function closeDialog() {
                map.graphics.clear();
                var widget = dijit.byId("tooltipDialog");
                if (widget) {
                    widget.destroy();
                }
            }
            stateLayer.redraw();
            stateLayer.setRenderer(renderer);
            map.addLayers([stateLayer]);
            registerStateLayer(map, stateLayer, hpGen);
        });

    }
}


function getAllMapDataChartforCLR() {
    cat = '111';
    mapLevel = 0;
    $.ajax({
        url: misUrl + 'category=111',
        type: 'POST',
        success: function (data) {
            console.log(data);
            //generate Table
            var data = JSON.parse(data);

            var customers = new Array();
            customers = data.datatable;
            //Create a HTML Table element.
            var table = document.createElement("TABLE");
            table.style.borderColor = "#ffff"; // Set border color to white
            table.className = "table table-striped-columns table-bordered table-success table-responsive";


            //Get the count of columns.
            var columnCount = customers[0].length;
            //Add the header row.
            var row = table.insertRow(-1);
            for (var i = 0; i < columnCount; i++) {
                var headerCell = document.createElement("TH");
                headerCell.innerHTML = customers[0][i];
                row.appendChild(headerCell);
            }

            //Add the data rows.
            for (var i = 1; i < customers.length; i++) {
                row = table.insertRow(-1);
                for (var j = 0; j < columnCount; j++) {
                    var cell = row.insertCell(-1);
                    cell.innerHTML = customers[i][j];
                }
            }

            var dvTable = document.getElementById("user_table");
            dvTable.innerHTML = "";
            dvTable.appendChild(table);
            //End Table

        }
    })

}
function getAllMapDataChartforMapDigitization() {
    cat = '301';
    mapLevel = 0;
    $.ajax({
        url: misUrl + 'category=301',
        type: 'POST',
        success: function (data) {
            // console.log(data.categories);
            //generate Table
            var data = JSON.parse(data);
            var customers = new Array();
            customers = data.datatable;
            //Create a HTML Table element.
            var table = document.createElement("TABLE");
            table.border = "1";
            table.className = "table table-striped-columns table-bordered table-success table-responsive";
            //Get the count of columns.
            var columnCount = customers[0].length;
            //Add the header row.
            var row = table.insertRow(-1);
            for (var i = 0; i < columnCount; i++) {
                var headerCell = document.createElement("TH");
                headerCell.innerHTML = customers[0][i];
                row.appendChild(headerCell);
            }
            //Add the data rows.
            for (var i = 1; i < customers.length; i++) {
                row = table.insertRow(-1);
                for (var j = 0; j < columnCount; j++) {
                    var cell = row.insertCell(-1);
                    cell.innerHTML = customers[i][j];
                }
            }
            var dvTable = document.getElementById("user_table");
            dvTable.innerHTML = "";
            dvTable.appendChild(table);
            //End Table
        }
    })
}
function getAllMapDataChartforMRR() {
    cat = '161';
    mapLevel = 0;
    $.ajax({

        url: misUrl + 'category=161',
        type: 'POST',
        success: function (data) {
            //generate Table
            var data = JSON.parse(data);
            var customers = new Array();
            customers = data.datatable;
            //Create a HTML Table element.
            var table = document.createElement("TABLE");
            table.border = "1";
            table.className = "table table-striped-columns table-bordered table-success table-responsive";
            //Get the count of columns.
            var columnCount = customers[0].length;
            //Add the header row.
            var row = table.insertRow(-1);
            for (var i = 0; i < columnCount; i++) {
                var headerCell = document.createElement("TH");
                headerCell.innerHTML = customers[0][i];
                row.appendChild(headerCell);
            }

            //Add the data rows.
            for (var i = 1; i < customers.length; i++) {
                row = table.insertRow(-1);
                for (var j = 0; j < columnCount; j++) {
                    var cell = row.insertCell(-1);
                    cell.innerHTML = customers[i][j];
                }
            }

            var dvTable = document.getElementById("user_table");
            dvTable.innerHTML = "";
            dvTable.appendChild(table);
            //End Table
        }

    })
}
function getAllMapDataChartforSurveyReservey() {
    cat = '401';
    mapLevel = 0;
    $.ajax({
        url: misUrl + 'category=401',
        type: 'POST',
        success: function (data) {
            var data = JSON.parse(data);
            //generate Table
            var customers = new Array();
            customers = data.datatable;
            //Create a HTML Table element.
            var table = document.createElement("TABLE");
            table.border = "1";
            table.className = "table table-striped-columns table-bordered table-success table-responsive";
            //Get the count of columns.
            var columnCount = customers[0].length;
            //Add the header row.
            var row = table.insertRow(-1);
            for (var i = 0; i < columnCount; i++) {
                var headerCell = document.createElement("TH");
                headerCell.innerHTML = customers[0][i];
                row.appendChild(headerCell);
            }

            //Add the data rows.
            for (var i = 1; i < customers.length; i++) {
                row = table.insertRow(-1);
                for (var j = 0; j < columnCount; j++) {
                    var cell = row.insertCell(-1);
                    cell.innerHTML = customers[i][j];
                }
            }

            var dvTable = document.getElementById("user_table");
            dvTable.innerHTML = "";
            dvTable.appendChild(table);
            //End Table





        }
    })
}
function getAllMapDataChartforSro() {
    cat = '501';
    mapLevel = 0;
    $.ajax({

        url: misUrl + 'category=501',
        type: 'POST',
        success: function (data) {
            // console.log(data.categories);
            var data = JSON.parse(data);

            //generate Table
            var customers = new Array();
            customers = data.datatable;
            //Create a HTML Table element.
            var table = document.createElement("TABLE");
            table.border = "1";
            table.className = "table table-striped-columns table-bordered table-success table-responsive";
            //Get the count of columns.
            var columnCount = customers[0].length;
            //Add the header row.
            var row = table.insertRow(-1);
            for (var i = 0; i < columnCount; i++) {
                var headerCell = document.createElement("TH");
                headerCell.innerHTML = customers[0][i];
                row.appendChild(headerCell);
            }

            //Add the data rows.
            for (var i = 1; i < customers.length; i++) {
                row = table.insertRow(-1);
                for (var j = 0; j < columnCount; j++) {
                    var cell = row.insertCell(-1);
                    cell.innerHTML = customers[i][j];
                }
            }

            var dvTable = document.getElementById("user_table");
            dvTable.innerHTML = "";
            dvTable.appendChild(table);
            //End Table
        }
    })
}
function getAllMapDataChartforSroLr() {
    cat = '601';
    mapLevel = 0;
    $.ajax({

        url: misUrl + 'category=601',
        type: 'POST',
        success: function (data) {
            // console.log(data.categories);

            //var data=JSON.parse(data);
            //generate Table
            var customers = new Array();
            customers = data.datatable;
            //Create a HTML Table element.
            var table = document.createElement("TABLE");
            table.border = "1";
            table.className = "table table-striped-columns table-bordered table-success table-responsive";
            //Get the count of columns.
            var columnCount = customers[0].length;
            //Add the header row.
            var row = table.insertRow(-1);
            for (var i = 0; i < columnCount; i++) {
                var headerCell = document.createElement("TH");
                headerCell.innerHTML = customers[0][i];
                row.appendChild(headerCell);
            }

            //Add the data rows.
            for (var i = 1; i < customers.length; i++) {
                row = table.insertRow(-1);
                for (var j = 0; j < columnCount; j++) {
                    var cell = row.insertCell(-1);
                    cell.innerHTML = customers[i][j];
                }
            }

            var dvTable = document.getElementById("user_table");
            dvTable.innerHTML = "";
            dvTable.appendChild(table);
            //End Table
        }
    })
}
function getAllMapDataChartforVillagesCadastralMapLinkedwithRoR() {
    cat = '701';
    mapLevel = 0;
    $.ajax({

        url: misUrl + 'category=701',
        type: 'POST',
        success: function (data) {
            // console.log(data.categories);

            //var data=JSON.parse(data);
            //generate Table
            var customers = new Array();
            customers = data.datatable;
            //Create a HTML Table element.
            var table = document.createElement("TABLE");
            table.border = "1";
            table.className = "table table-striped-columns table-bordered table-success table-responsive";
            //Get the count of columns.
            var columnCount = customers[0].length;
            //Add the header row.
            var row = table.insertRow(-1);
            for (var i = 0; i < columnCount; i++) {
                var headerCell = document.createElement("TH");
                headerCell.innerHTML = customers[0][i];
                row.appendChild(headerCell);
            }

            //Add the data rows.
            for (var i = 1; i < customers.length; i++) {
                row = table.insertRow(-1);
                for (var j = 0; j < columnCount; j++) {
                    var cell = row.insertCell(-1);
                    cell.innerHTML = customers[i][j];
                }
            }

            var dvTable = document.getElementById("user_table");
            dvTable.innerHTML = "";
            dvTable.appendChild(table);
            //End Table
        }
    })
}
function getAllMapDataChartforRCCMS() {
    cat = '801';
    mapLevel = 0;
    $.ajax({

        url: misUrl + 'category=801',
        type: 'POST',
        success: function (data) {
            // console.log(data.categories);

            //var data=JSON.parse(data);
            //generate Table
            var customers = new Array();
            customers = data.datatable;
            //Create a HTML Table element.
            var table = document.createElement("TABLE");
            table.border = "1";
            table.className = "table table-striped-columns table-bordered table-success table-responsive";
            //Get the count of columns.
            var columnCount = customers[0].length;
            //Add the header row.
            var row = table.insertRow(-1);
            for (var i = 0; i < columnCount; i++) {
                var headerCell = document.createElement("TH");
                headerCell.innerHTML = customers[0][i];
                row.appendChild(headerCell);
            }

            //Add the data rows.
            for (var i = 1; i < customers.length; i++) {
                row = table.insertRow(-1);
                for (var j = 0; j < columnCount; j++) {
                    var cell = row.insertCell(-1);
                    cell.innerHTML = customers[i][j];
                }
            }

            var dvTable = document.getElementById("user_table");
            dvTable.innerHTML = "";
            dvTable.appendChild(table);
            //End Table
        }
    })
}
function getAllMapDataChartforAadhar() {
    cat = '901';
    mapLevel = 0;
    $.ajax({

        url: misUrl + 'category=901',
        type: 'POST',
        success: function (data) {
            // console.log(data.categories);

            //var data=JSON.parse(data);
            //generate Table
            var customers = new Array();
            customers = data.datatable;
            //Create a HTML Table element.
            var table = document.createElement("TABLE");
            table.border = "1";
            table.className = "table table-striped-columns table-bordered table-success table-responsive";
            //Get the count of columns.
            var columnCount = customers[0].length;
            //Add the header row.
            var row = table.insertRow(-1);
            for (var i = 0; i < columnCount; i++) {
                var headerCell = document.createElement("TH");
                headerCell.innerHTML = customers[0][i];
                row.appendChild(headerCell);
            }

            //Add the data rows.
            for (var i = 1; i < customers.length; i++) {
                row = table.insertRow(-1);
                for (var j = 0; j < columnCount; j++) {
                    var cell = row.insertCell(-1);
                    cell.innerHTML = customers[i][j];
                }
            }

            var dvTable = document.getElementById("user_table");
            dvTable.innerHTML = "";
            dvTable.appendChild(table);
            //End Table
        }
    })
}

function downloadimage() {

    /*var container = document.getElementById("image-wrap");*/ /*specific element on page*/
    var container = document.getElementById("mapdivparent");
    //                var container = document.getElementsByClassName("save-map");

    html2canvas(container, {
        allowTaint: true, scrollX: -window.scrollX,
        scrollY: -window.scrollY
    }).then(function (canvas) {

        var link = document.createElement("a");
        document.body.appendChild(link);
        //document.getElementById("map").style.marginLeft = "150px";
        link.download = "download_map.PNG";
        link.href = canvas.toDataURL();
        console.log(link.href);
        link.target = '_blank';
        link.click();
    });
}

function handleBoxClick(anchor) {
    // Show the GIF overlay
    const gifOverlay = document.getElementById('gifOverlay');
    gifOverlay.style.display = 'flex'; // Show the overlay

    // After 5 milliseconds, hide the overlay and set the active state
    setTimeout(function () {
        gifOverlay.style.display = 'none'; // Hide the overlay

        // Remove active class and reset background color from all boxes
        document.querySelectorAll('.small-box').forEach(function (box) {
            box.classList.remove('active-box');
            box.style.backgroundColor = ''; // Reset background color to default
        });

        // Add active class to the clicked box
        const parentBox = anchor.closest('.small-box');
        parentBox.classList.add('active-box');
        parentBox.style.backgroundColor = '#007bff'; // Set desired active background color
    }, 300); // 5 milliseconds delay (adjust as needed)
}


