$(document).ready(function () {
    document.getElementById("maptitle").innerHTML = "Percentage of villages with completed CLR";
    document.getElementById("tabletitle").innerHTML = "Percentage of villages with completed CLR";
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
        document.getElementById("maptitle").innerHTML = "Percentage of villages with completed CLR";
        document.getElementById("tabletitle").innerHTML = "Percentage of villages with completed CLR";
        statemap("0", 0, 0, 0, 0);
        getAllMapDataChartforCLR();
    } else if (id === "MapDigitization") {
        document.getElementById("maptitle").innerHTML = "Percentage of Map digitization";
        document.getElementById("tabletitle").innerHTML = "Percentage of Map digitization";
        statemap("30", 0, 0, 0, 0);
        getAllMapDataChartforMapDigitization();
    }
    else if (id === "mrr") {
        document.getElementById("maptitle").innerHTML = "Percentage of  Tehsil with completed MRR";
        document.getElementById("tabletitle").innerHTML = "Percentage of  Tehsil with completed MRR";
        statemap("6", 0, 0, 0, 0);
        getAllMapDataChartforMRR();
    }
    else if (id === "surveyReservey") {
        document.getElementById("maptitle").innerHTML = "Percentage of Villages with completed Survey/Re-Survey";
        document.getElementById("tabletitle").innerHTML = "Percentage of Villages with completed Survey/Re-Survey";
        statemap("40", 0, 0, 0, 0);
        getAllMapDataChartforSurveyReservey();
    }
    else if (id === "sro") {
        document.getElementById("maptitle").innerHTML = "Percentage of SRO Computerized";
        document.getElementById("tabletitle").innerHTML = "Percentage of SRO Computerized";
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
        document.getElementById("maptitle").innerHTML = "RCCMS";
        document.getElementById("tabletitle").innerHTML = "RCCMS";
        statemap("80", 0, 0, 0, 0);
        getAllMapDataChartforRCCMS();
    }
    else if (id === "linkedwithaadhaar") {
        document.getElementById("maptitle").innerHTML = "Aadhaar Linkage Status";
        document.getElementById("tabletitle").innerHTML = "Aadhaar Linkage Status";
        statemap("90", 0, 0, 0, 0);
        getAllMapDataChartforAadhar();
    }
    handleBoxClick(anchor);
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
    var serviceUrl = 'https://mapservice.gov.in/gismapservice/rest/services/BharatMapService/Admin_Boundary_Village/MapServer';
    var serviceUrlToken = '?Token=n1OHCwv4orL1CquVESNMXRMgb2oQXq6sh8DPlD7YCc9MhZUwsNNK7dwFt7VKAK3H';
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


