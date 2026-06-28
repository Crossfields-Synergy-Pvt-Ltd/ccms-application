module.exports = function(config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine'],
    files: [
      'src/test/javascript/mocks/jquery-stub.js',
      'node_modules/angular/angular.js',
      'node_modules/angular-mocks/angular-mocks.js',
      'node_modules/angular-ui-router/release/angular-ui-router.js',
      'node_modules/angular-resource/angular-resource.js',
      'node_modules/angular-sanitize/angular-sanitize.js',
      'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js',
      'src/test/javascript/mocks/google-maps-stub.js',
      'src/test/javascript/mocks/module-stubs.js',
      'src/main/webapp/app/**/*.js',
      'src/test/javascript/**/*.test.js'
    ],
    exclude: [],
    preprocessors: {},
    reporters: ['progress'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['ChromeHeadless'],
    customLaunchers: {
      ChromeHeadless: {
        base: 'Chrome',
        flags: ['--headless', '--disable-gpu', '--no-sandbox', '--disable-setuid-sandbox']
      }
    },
    singleRun: false,
    concurrency: Infinity
  });
};
