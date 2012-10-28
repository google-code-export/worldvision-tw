var WV = {
  init: function wv_init() {
    this.browserDetect();
  },

  browserDetect: function wv_browserDetect() {
    if ($.browser.msie && ($.browser.version == '7.0' || $.browser.version == '6.0')) {
      $('#browser-warning').show();
    } else {
      $('#browser-warning').hide();
    }
  }
};

WV.init();
