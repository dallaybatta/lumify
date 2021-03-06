module.exports = function(grunt) {

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),

    bower: {
      install: {
          options: {
              targetDir: './libs',
              install: true,
              copy: false,
          }
      },
      prune: {
          options: {
              targetDir: './libs',
              copy: false,
              offline: true
          }
      }
    },

    exec: {
        build_openlayers: {
            command: 'python build.py -c none full ../OpenLayers.debug.js',
            stdout: false,
            cwd: 'libs/openlayers/build'
        },
        build_cytoscape: {
            command: 'make minify',
            stdout: false,
            cwd: 'libs/cytoscape.js'
        }
    },

    less: {
        development: {
            files: {
                "css/lumify.css": "less/lumify.less"
            },
            options: {
                paths: ["less"]
            }
        },
        production: {
            files: {
                "css/lumify.css": "less/lumify.less"
            },
            options: {
                paths: ["less"],
                compress: true,
                sourceMap: true,
                sourceMapFilename: 'css/lumify.css.map',
                sourceMapURL: 'lumify.css.map',
                sourceMapRootpath: '/',
                dumpLineNumbers: 'all'
            }
        }
    },

    requirejs: {
        options: {
            mainConfigFile: 'js/require.config.js',
            dir: 'jsc',
            baseUrl: 'js',
            preserveLicenseComments: false,
            removeCombined: false,
            /*
            modules: [
                { name: 'lumify' },
                { name: 'app' },
                { name: 'appFullscreenDetails' },
                { name: 'detail/artifact/artifact' },
                { name: 'detail/entity/entity' }
            ]
                */
        },
        development: {
            options: {
                logLevel: 2,
                optimize: 'none',
                keepBuildDir: true,
            }
        },
        production: {
            options: {
                logLevel: 0,
                optimize: 'uglify2',
                generateSourceMaps: true,
            }
        }
    },

    concurrent: {
        development: ['requirejs:development', 'less:development'],
        selenium: ['mochaSelenium:chrome', 'mochaSelenium:firefox'],
        tests: ['karma', 'jshint', 'mochaSelenium:chrome', 'mochaSelenium:firefox']
    },

    jshint: {
        development: {
            files: {
                src: ['js/**/*.js']
            },
            options: {
                browser: true,
                '-W033': true, // Semicolons
                '-W040': true, // Ignore Strict violations from flight idioms
            }
        }
    },

    watch: {
        css: {
            files: ['less/**/*.less', 'libs/**/*.css', 'libs/**/*.less'],
            tasks: ['less:development', 'notify:css'],
            options: {
                spawn: true
            }
        },
        scripts: {
            files: ['Gruntfile.js', 'js/**/*.js', 'js/**/*.ejs'],
            tasks: ['requirejs:development', 'notify:js'],
            options: {
                spawn: true
            }
        },
        lint: {
            files: ['Gruntfile.js', 'js/**/*.js'],
            tasks: ['jshint:development'],
            options: {
                spawn: true
            }
        }
    },

    notify: {
        js: {
            options: {
                title: 'Lumify',
                message: 'RequireJS finished',
            }
        },
        css: {
            options: {
                title: 'Lumify',
                message: 'Less finished',
            }
        }
    },

    mochaSelenium: {
        options: { 
            screenshotAfterEach: true,
            screenshotDir: 'test/reports',
            reporter: 'spec', // doc for html
            viewport: { width: 900, height: 700 },
            timeout: 30e3,
            slow: 10e3,
            usePromises: true,
            useChaining: true,
            ignoreLeaks: false
        },
        safari:  { src: ['test/functional/spec/**/*.js' ], options: { browserName: 'safari' } },
        firefox: { src: ['test/functional/spec/**/*.js' ], options: { browserName: 'firefox' } },
        chrome:  { src: ['test/functional/spec/**/*.js' ], options: { browserName: 'chrome' } }
    },

    karma: {
        unit: {
            configFile: 'karma.conf.js',
            runnerPort: 9999,
            singleRun: true
        }
    }
  });

  grunt.loadNpmTasks('grunt-bower-task');
  grunt.loadNpmTasks('grunt-exec');
  grunt.loadNpmTasks('grunt-concurrent');
  grunt.loadNpmTasks('grunt-contrib-less');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-requirejs');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-notify');
  grunt.loadNpmTasks('grunt-mocha-selenium');
  grunt.loadNpmTasks('grunt-karma');

  grunt.registerTask('deps', 'Install Webapp Dependencies', ['bower:install', 'bower:prune', 'exec']);

  grunt.registerTask('test:functional', 'Run JavaScript Functional Tests', ['concurrent:selenium']);
  grunt.registerTask('test:unit', 'Run JavaScript Unit Tests', ['karma']);
  grunt.registerTask('test:lint', 'Run JavaScript Lint Tests', ['jshint']);
  grunt.registerTask('test', 'Run unit and functional tests', ['concurrent:tests'])

  grunt.registerTask('development', 'Build js/less for development', ['less:development', 'requirejs:development']);
  grunt.registerTask('production', 'Build js/less for production', ['less:production', 'requirejs:production']);

  grunt.registerTask('default', ['development']);
};
