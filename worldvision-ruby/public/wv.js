var WorldVision = {
  parseURL: function wv_parseURL() {
    this.URL = $.url.parse();
  },

  bindEvents: function wv_bind() {
    $('ul.nav.nav-list a').click(function(event) {
      var target = $(this).attr('href');
      $(this).parent().siblings().removeClass('active');
      $(this).parent().addClass('active');
      $(target).siblings().hide();
      $(target).show();

      event.preventDefault();
    });
  },

  view: function wv_view() {
    if (this.URL.params && this.URL.params.em_start) {
      $('.pagination:visible li.active').removeClass('active');
      $('.pagination:visible ul:nth-child('+this.URL.params.em_start+')').addClass('active');
    }

    if (this.URL.params && this.URL.params.type) {
    
    }
  },

  init: function wv_init() {
    this.parseURL();
    this.bindEvents();
    this.view();
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

WorldVision.init();
