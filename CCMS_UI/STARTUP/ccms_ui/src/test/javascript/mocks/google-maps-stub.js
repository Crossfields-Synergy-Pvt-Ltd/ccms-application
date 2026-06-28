// Stub Google Maps API for map controllers
window.google = {
  maps: {
    LatLng: function(lat, lng) {
      this.lat = function() { return parseFloat(lat); };
      this.lng = function() { return parseFloat(lng); };
    },
    Map: function(element, options) {
      this.setCenter = function() {};
      this.setZoom = function() {};
      this.fitBounds = function() {};
      this.panTo = function() {};
    },
    Marker: function(options) {
      this.setMap = function() {};
      this.addListener = function() {};
    },
    InfoWindow: function(options) {
      this.open = function() {};
      this.close = function() {};
    },
    event: {
      addListener: function() {},
      clearListeners: function() {}
    },
    LatLngBounds: function() {
      this.extend = function() {};
      this.getCenter = function() { return new window.google.maps.LatLng(16.4792, 80.5469); };
    },
    Animation: { DROP: 1 },
    MapTypeId: { ROADMAP: 'roadmap' },
    MapTypeControlStyle: { HORIZONTAL_BAR: 1, DROPDOWN_MENU: 2, DEFAULT: 0 },
    ZoomControlStyle: { SMALL: 1, LARGE: 2, DEFAULT: 0 },
    NavigationControlStyle: { ZOOM_PAN: 1, SMALL: 2, ANDROID: 3, DEFAULT: 0 },
    ControlPosition: { TOP_LEFT: 1, TOP_RIGHT: 2, BOTTOM_LEFT: 3, BOTTOM_RIGHT: 4, LEFT_CENTER: 5, RIGHT_CENTER: 6, TOP_CENTER: 7, BOTTOM_CENTER: 8 },
    SymbolPath: { CIRCLE: 0 },
    Point: function(x, y) { this.x = x; this.y = y; },
    Size: function(w, h) { this.width = w; this.height = h; }
  }
};
