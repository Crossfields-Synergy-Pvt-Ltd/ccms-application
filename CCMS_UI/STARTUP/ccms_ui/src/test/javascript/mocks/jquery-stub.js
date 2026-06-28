// Minimal $ stub for navibar-controllers.js which references $(document).ready
// We do NOT set window.jQuery so Angular falls back to jqLite.
window.$ = function() {
  var stub = {
    ready: function(fn) { if (typeof fn === 'function') fn(); return stub; },
    css: function() { return stub; },
    on: function() { return stub; },
    off: function() { return stub; },
    trigger: function() { return stub; },
    find: function() { return stub; },
    each: function() { return stub; },
    text: function() { return ''; },
    val: function() { return ''; },
    html: function() { return ''; },
    attr: function() { return ''; },
    data: function() { return {}; },
    remove: function() { return stub; },
    addClass: function() { return stub; },
    removeClass: function() { return stub; },
    hasClass: function() { return false; },
    toggleClass: function() { return stub; },
    show: function() { return stub; },
    hide: function() { return stub; },
    width: function() { return 0; },
    height: function() { return 0; },
    innerWidth: function() { return 0; },
    innerHeight: function() { return 0; },
    outerWidth: function() { return 0; },
    outerHeight: function() { return 0; },
    offset: function() { return {top: 0, left: 0}; },
    position: function() { return {top: 0, left: 0}; },
    scrollTop: function() { return 0; },
    scrollLeft: function() { return 0; },
    slideUp: function() { return stub; },
    slideDown: function() { return stub; },
    fadeIn: function() { return stub; },
    fadeOut: function() { return stub; },
    animate: function() { return stub; },
    delegate: function() { return stub; },
    tooltip: function() { return stub; },
    select2: function() { return stub; },
    append: function() { return stub; },
    prepend: function() { return stub; },
    empty: function() { return stub; }
  };
  return stub;
};
window.$.ajax = function() {
  return { done: function() { return this; }, fail: function() { return this; }, always: function() { return this; } };
};
window.$.getJSON = function() {
  return { done: function() { return this; }, fail: function() { return this; }, always: function() { return this; } };
};
// Stub moment.js for daterangepicker and other date utilities
window.moment = function() {
  return {
    format: function() { return ''; },
    startOf: function() { return this; },
    endOf: function() { return this; },
    add: function() { return this; },
    subtract: function() { return this; },
    diff: function() { return 0; },
    isSame: function() { return false; },
    isBefore: function() { return false; },
    isAfter: function() { return false; },
    clone: function() { return window.moment(); },
    valueOf: function() { return Date.now(); },
    unix: function() { return Math.floor(Date.now() / 1000); },
    toDate: function() { return new Date(); },
    locale: function() { return this; }
  };
};
window.moment.locale = function() {};
window.moment.utc = window.moment;
window.moment.fn = {};
