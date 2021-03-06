define(['flight/lib/registry', 'jquery'],function(registry) {
    'use strict';

    $.fn.lookupComponent = function(instanceConstructor) {
        return _lookupComponent(this[0], instanceConstructor);
    };

    $.fn.teardownComponent = function(instanceConstructor) {
        var instance = _lookupComponent(this[0], instanceConstructor);
        if (instance) {
            instance.teardown();
        }
        return this;
    };

    $.fn.teardownAllComponentsWithMixin = function(Mixin) {
        var results = registry.findInstanceInfoByNode(this[0]);
        for (var i = 0; i < results.length; ++i) {
            var instance = results[i].instance;
            if (instance.mixedIn) {
                for (var j = 0; j < instance.mixedIn.length; j++) {
                    if (instance.mixedIn[j] === Mixin) {
                        instance.teardown();
                        break;
                    }
                }
            }
        }
        return this;
    };

    $.fn.teardownAllComponents = function() {
        var results = registry.findInstanceInfoByNode(this[0]);
        for (var i = 0; i < results.length; ++i) {
            results[i].instance.teardown();
        }
        return this;
    };

    function _lookupComponent (elem, instanceConstructor) {
        var results = registry.findInstanceInfoByNode(elem);
        for (var i = 0; i < results.length; ++i) {
            if (results[i].instance.constructor === instanceConstructor) {
                return results[i].instance;
            }
        }
        return false;
    }

});
