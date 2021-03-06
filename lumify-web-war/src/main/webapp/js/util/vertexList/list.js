
define([
    'flight/lib/component',
    'flight/lib/registry',
    'data',
    'tpl!./list',
    'tpl!./item',
    'tpl!util/alert',
    'util/previews',
    'util/video/scrubber',
    'util/jquery.withinScrollable',
    'util/jquery.ui.draggable.multiselect'
], function(defineComponent, registry, appData, template, vertexTemplate, alertTemplate, previews, VideoScrubber) {
    'use strict';

    return defineComponent(List);

    function hasPreview(vertex) {
        return (/^(image|video)$/).test(vertex.concept.displayType) || 
            !!vertex.properties._glyphIcon;
    }

    function List() {

        this.defaultAttrs({
            itemSelector: '.vertex-item',
            infiniteScrolling: false
        });

        this.stateForVertex = function(vertex) {
            var inWorkspace = appData.inWorkspace(vertex);
            return {
                inGraph: inWorkspace,
                inMap: inWorkspace && !!(
                        vertex.properties.geoLocation ||
                        (vertex.location || (vertex.locations && vertex.locations.length)) ||
                        (vertex.properties.latitude && vertex.properties.longitude)
                )
            };
        };

        this.classNameMapForVertices = function(vertices) {
            var self = this,
                classNamesForVertex = {};

            vertices.forEach(function(v) {

                // Check if this vertex is in the graph/map
                self.classNameLookup[v.id] = (self.classNameLookup[v.id] || ('vId' + (++self.classNameIndex)));

                var classes = [self.classNameLookup[v.id]];

                var vertexState = self.stateForVertex(v);
                if ( vertexState.inGraph ) classes.push('graph-displayed');
                if ( vertexState.inMap ) classes.push('map-displayed');

                if (hasPreview(v)) {
                    classes.push('has_preview');
                }

                classNamesForVertex[v.id] = classes.join(' ');
            });

            return classNamesForVertex;
        };

        this.after('initialize', function() {
            var self = this;

            this.classNameIndex = 0;
            this.classNameLookup = {};

            this.$node
                .addClass('vertex-list')
                .html(template({ 
                    vertices:this.attr.vertices,
                    infiniteScrolling: this.attr.infiniteScrolling && this.attr.total !== this.attr.vertices.length,
                    classNamesForVertex: this.classNameMapForVertices(this.attr.vertices)
                }));

            this.attachEvents();


            this.loadVisibleResultPreviews = _.debounce(this.loadVisibleResultPreviews.bind(this), 1000);
            this.loadVisibleResultPreviews();

            this.triggerInfiniteScrollRequest = _.debounce(this.triggerInfiniteScrollRequest.bind(this), 1000);
            this.triggerInfiniteScrollRequest();

            this.setupDraggables();

            this.onObjectsSelected(null, { edges:[], vertices:appData.selectedVertices});

            this.on('selectAll', this.onSelectAll);
            this.on('down', this.move);
            this.on('up', this.move);

            _.defer(function() {
                this.$node.scrollTop(0);
            }.bind(this))

        });

        this.move = function(e, data) {
            var previousSelected = this.$node.find('.active')[e.type === 'up' ? 'first' : 'last'](),
                moveTo = previousSelected[e.type === 'up' ? 'prev' : 'next']('.vertex-item');

            if (moveTo.length) {

                var selected = [];

                if (data.shiftKey) {
                    selected = selected.concat(appData.selectedVertices);
                    selected.push(appData.vertex(moveTo.data('vertexId')));
                } else {
                    selected.push(appData.vertex(moveTo.data('vertexId')));
                }

                this.trigger(document, 'defocusVertices');
                this.trigger('selectObjects', { vertices:selected });
            }
        };

        this.onSelectAll = function(e) {
            e.stopPropagation();

            var items = this.$node.find('.vertex-item').addClass('active');
            this.selectItems(items);
        };

        this.after('teardown', function() {
            this.$node.off('mouseenter mouseleave');
            this.scrollNode.off('scroll.vertexList');
            this.$node.empty();
        });

        this.attachEvents = function() {
            this.scrollNode = this.$node;
            while (this.scrollNode.length && this.scrollNode.css('overflow') !== 'auto') {
                this.scrollNode = this.scrollNode.parent();
            }
            this.scrollNode.on('scroll.vertexList', this.onResultsScroll.bind(this));

            this.$node.on('mouseenter mouseleave', '.vertex-item', this.onHoverItem.bind(this));

            this.on(document, 'verticesAdded', this.onVerticesUpdated);
            this.on(document, 'verticesUpdated', this.onVerticesUpdated);
            this.on(document, 'verticesDeleted', this.onVerticesDeleted);
            this.on(document, 'objectsSelected', this.onObjectsSelected);
            this.on(document, 'switchWorkspace', this.onWorkspaceClear);
            this.on(document, 'workspaceDeleted', this.onWorkspaceClear);
            this.on(document, 'workspaceLoaded', this.onWorkspaceLoaded);
            this.on('addInfiniteVertices', this.onAddInfiniteVertices);
        };

        this.setupDraggables = function() {
            this.applyDraggable(this.$node.find('a'));
            this.$node.droppable({ accept:'*', tolerance:'pointer' });
        };

        this.onHoverItem = function(evt) {
            if (this.disableHover === 'defocused') {
                return;
            } else if (this.disableHover) {
                this.disableHover = 'defocused';
                return this.trigger(document, 'defocusVertices');
            }

            var id = $(evt.target).closest('.vertex-item').data('vertexId');
            if (evt.type == 'mouseenter' && id) {
                this.trigger(document, 'focusVertices', { vertexIds:[id] });
            } else {
                this.trigger(document, 'defocusVertices');
            }
        };

        this.onResultsScroll = function(e) {
            if (!this.disableHover) {
                this.disableHover = true;
            }

            this.loadVisibleResultPreviews();

            if (this.attr.infiniteScrolling) {
                this.triggerInfiniteScrollRequest();
            }
        };

        this.triggerInfiniteScrollRequest = function() {
            if (!this.attr.infiniteScrolling) return;

            var loadingListElement = this.$node.find('.infinite-loading');

            if (this.scrollNode.length) {
                loadingListElement = loadingListElement.withinScrollable(this.scrollNode);
            }

            if (loadingListElement.length) {
                var data = { conceptType: this.attr.verticesConceptId };
                if (!this.offset) this.offset = this.attr.vertices.length;
                data.paging = {
                    offset: this.offset
                };
                this.trigger('infiniteScrollRequest', data);
            }
        };

        this.onAddInfiniteVertices = function(evt, data) {
            var loading = this.$node.find('.infinite-loading');

            if (!data.success) {
                loading.html(alertTemplate({ error: 'Error loading search results' }));
                this.attr.infiniteScrolling = false;
            } else if (data.vertices.length === 0) {
                loading.remove();
                this.attr.infiniteScrolling = false;
            } else {
                this.offset += data.vertices.length;
                var clsMap = this.classNameMapForVertices(data.vertices),
                    added = data.vertices.map(function(vertex) {
                        return vertexTemplate({
                            vertex:vertex,
                            classNamesForVertex: clsMap
                        });
                    });

                var lastItem = loading.prev();

                loading.before(added);

                var total = data.total || this.attr.total || 0;
                if (total === this.$node.find('.vertex-item').length) {
                    loading.remove();
                    this.attr.infiniteScrolling = false;
                } else {
                    this.triggerInfiniteScrollRequest();
                }

                this.loadVisibleResultPreviews();

                this.applyDraggable(this.$node.find('a'));
            }
        };

        this.loadVisibleResultPreviews = function() {
            var self = this;

            this.disableHover = false;
            if ( !this.previewQueue ) {
                this.previewQueue = previews.createQueue('vertexList', { maxConcurrent: 1 });
            }

            var lisVisible = this.$node.find('.nav-list').children('li');
            if (this.scrollNode.length) {
                lisVisible = lisVisible.withinScrollable(this.scrollNode);
            }

            lisVisible.each(function() {
                var li = $(this),
                    vertex = appData.vertex(li.data('vertexId'));
                
                if (!vertex) return;

                if (hasPreview(vertex) && !li.data('preview-loaded')) {

                        if (li.data('previewloaded')) return;

                        li.data('previewloaded', true).addClass('preview-loading');

                        if (vertex.properties._glyphIcon && vertex.properties._glyphIcon.value) {
                            li.removeClass('preview-loading')
                                .data('preview-loaded', true)
                                .find('.preview').html("<img src='" + vertex.properties._glyphIcon.value + "' />");
                        } else {
                            previews.generatePreview(vertex, { width: 200 }, function(poster, frames) {
                                li.removeClass('preview-loading')
                                  .data('preview-loaded', true);

                                if(vertex.concept.displayType === 'video') {
                                    VideoScrubber.attachTo(li.find('.preview'), {
                                        posterFrameUrl: poster,
                                        videoPreviewImageUrl: frames
                                    });
                                } else if(vertex.concept.displayType === 'image') {
                                    li.find('.preview').html("<img src='" + poster + "' />");
                                }
                            });
                        }
                    }
            });
        };

        this.applyDraggable = function(el) {
            var self = this;

            el.draggable({
                helper:'clone',
                appendTo: 'body',
                revert: 'invalid',
                revertDuration: 250,
                scroll: false,
                zIndex: 100,
                distance: 10,
                multi: true,
                otherDraggablesClass: 'vertex-dragging',
                start: function(ev, ui) {
                    $(ui.helper).addClass('vertex-dragging');
                },
                selection: function(ev, ui) {
                    self.selectItems(ui.selected);
                }
            });
        };

        this.selectItems = function(items) {
            var vertices = appData.vertices(items.map(function() {
                    return $(this).data('vertexId');
                }).toArray());

            if (vertices.length > 1) {
                vertices.forEach (function (vertex) {
                    vertex.workspace = {
                        selected: true
                    };
                });
            }
            this.trigger(document, 'defocusVertices');
            this.trigger('selectObjects', { vertices:vertices });
        };

        this.onWorkspaceLoaded = function(evt, workspace) {
            this.onVerticesUpdated(evt, workspace.data || {});
        };

        // Track changes to vertices so we display the "Displayed in Graph" icon
        // in search results
        this.toggleItemIcons = function(id, data) {
            this.$node
                .find('li.' + this.classNameLookup[id])
                .toggleClass('graph-displayed', data.inGraph)
                .toggleClass('map-displayed', data.inMap);
        };

        // Switching workspaces should clear the icon state and vertices
        this.onWorkspaceClear = function() {
            this.$node.find('li.graph-displayed').removeClass('graph-displayed');
            this.$node.find('li.map-displayed').removeClass('map-displayed');
        };

        this.onVerticesUpdated = function(event, data) {
            var self = this;
            (data.vertices || []).forEach(function(vertex) {
                self.toggleItemIcons(vertex.id, self.stateForVertex(vertex));
                var currentAnchor = self.$node.find('li.' + self.classNameLookup[vertex.id]).children('a'),
                    newAnchor = $(vertexTemplate({
                        vertex: vertex,
                        classNamesForVertex: self.classNameMapForVertices([vertex]),
                    })).children('a'),
                    currentHtml = currentAnchor.html(),
                    src = (vertex.properties._glyphIcon && vertex.properties._glyphIcon.value) ||
                        (
                            vertex.concept.displayType === 'image' ? ('/artifact/' + vertex.id + '/thumbnail') :
                            vertex.concept.displayType === 'video' ? ('/artifact/' + vertex.id + '/poster-frame') : null
                        );

                if (src) {
                    $('<img/>').attr('src', src).appendTo(newAnchor.find('.preview'));
                }

                var newHtml = newAnchor.html();
                if (currentAnchor.length && newHtml !== currentHtml) {
                    currentAnchor.html(newHtml).closest('.vertex-item').toggleClass('has_preview', !!src);
                }
            });
        };

        this.onVerticesDeleted = function(event, data) {
            var self = this;
            (data.vertices || []).forEach(function(vertex) {
                self.toggleItemIcons(vertex.id, { inGraph:false, inMap:false });
            });
        };

        this.onObjectsSelected = function(event, data) {
            this.$node.find('.active').removeClass('active');

            var self = this,
                ids = _.chain(data.vertices)
                    .map(function(v) { return '.' + self.classNameLookup[v.id]; })
                    .value().join(',');

            $(ids).addClass('active');
        };
    }
});
