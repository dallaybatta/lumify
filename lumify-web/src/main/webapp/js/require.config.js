
var require = {
    baseUrl: 'jsc',
    //urlArgs: "cache-bust=" +  Date.now(),
    paths: {
        'arbor': '../libs/cytoscape.js/lib/arbor',
        'async' : '../libs/requirejs-plugins/src/async',
        'atmosphere': '../libs/jquery.atmosphere/jquery.atmosphere',
        'bootstrap': '../libs/bootstrap/docs/assets/js/bootstrap',
        'bootstrap-datepicker': '../libs/bootstrap-datepicker/js/bootstrap-datepicker',
        'colorjs': '../libs/color-js/color',
        'cytoscape': '../libs/cytoscape.js/build/cytoscape',
        'd3': '../libs/d3/d3',
        'easing': '../libs/jquery.easing/js/jquery.easing',
        'ejs':  '../libs/ejs/ejs',
        'es5shim': '../libs/es5-shim/es5-shim',
        'es5sham': '../libs/es5-shim/es5-sham',
        'flight': '../libs/flight',
        'goog': '../libs/requirejs-plugins/src/goog',
        'intercom': '../libs/intercom/intercom',
        'jquery': '../libs/jquery/jquery',
        'jqueryui': '../libs/jquery-ui/ui/minified/jquery-ui.min',
        'openlayers': '../libs/openlayers/OpenLayers.debug',
        'pathfinding': '../libs/PathFinding.js/lib/pathfinding-browser',
        'propertyParser' : '../libs/requirejs-plugins/src/propertyParser',
        'scrollStop': '../libs/jquery-scrollstop/jquery.scrollstop',
        'sf': '../libs/sf/sf',
        'text': '../libs/requirejs-text/text',
        'three': '../libs/threejs/build/three.min',
        'tpl': '../libs/requirejs-ejs/rejs',
        'underscore': '../libs/underscore/underscore',
        'videojs': '../libs/video.js/video',
    },
    shim: {
        'atmosphere': { exports: 'jQuery' },
        'bootstrap': { exports: 'jQuery', deps:['jquery'] },
        'bootstrap-datepicker': { exports: 'jQuery', deps:['bootstrap'] },
        'colorjs': { init: function() { return this.net.brehaut.Color; } },
        'cytoscape': { exports: 'cytoscape', deps:['arbor'] },
        'd3': { exports: 'd3' },
        'easing': { exports: 'jQuery', deps:['jquery'] },
        'ejs': { exports: 'ejs' },
        'intercom': { exports:'Intercom' },
        'jquery': { exports:'jQuery' },
        'jqueryui': { exports:'jQuery', deps:['jquery'] },
        'openlayers': { exports: 'OpenLayers', deps:['goog!maps,3,other_params:sensor=false'] },
        'pathfinding': { exports: 'PF' },
        'scrollStop': { exports: 'jQuery', deps:['jquery'] },
        'three': { exports: 'THREE' },
        'underscore': { exports: '_' },
        'videojs': { exports: 'videojs' }
    }
};


/*
try {
// For testing to use this configuration test/runner/main.js
if ('define' in window) {
    define([], function() {
        return require;
    });
}
} catch() { }
*/