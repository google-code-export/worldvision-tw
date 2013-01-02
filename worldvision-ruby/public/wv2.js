(function() {
  var URL = {};

  function init() {
    // Then parse the URL
    URL = $.url.parse();

    // No letter status specified, save the count of letters in cookie
    if (! URL.params || ! 'letter_status' in URL.params) {
      if (URL.params && URL.params.type && URL.params.type == 'chi') {
        $.cookie('all-chi', $('#all-count').text());
        $.cookie('uncliamed-chi', $('#unclaimed-count').text());
        $.cookie('claimed-chi', $('#claimed-count').text());
        $.cookie('emergent-chi', $('#emergent-count').text());
        $.cookie('returned-chi', $('#returned-count').text());
      } else {
        $.cookie('all', $('#all-count').text());
        $.cookie('uncliamed', $('#unclaimed-count').text());
        $.cookie('claimed', $('#claimed-count').text());
        $.cookie('emergent', $('#emergent-count').text());
        $.cookie('returned', $('#returned-count').text());
      }
      $('#all').addClass('active').siblings().removeClass('active');
    } else {
      // Restore the count
      if (URL.params && URL.params.type && URL.params.type == 'chi') {
        if ($.cookie('all-chi')) {
          $('#all-count').text($.cookie('all-chi'));
        }
        if ($.cookie('unclaimed-chi')) {
          $('#unclaimed-count').text($.cookie('unclaimed-chi'));
        }
        if ($.cookie('claimed-chi')) {
          $('#claimed-count').text($.cookie('claimed-chi'));
        }
        if ($.cookie('emergent-chi')) {
          $('#emergent-count').text($.cookie('emergent-chi'));
        }
        if ($.cookie('returned-chi')) {
          $('#returned-count').text($.cookie('returned-chi'));
        }
      } else {
        if ($.cookie('all')) {
          $('#all-count').text($.cookie('all'));
        }
        if ($.cookie('unclaimed')) {
          $('#unclaimed-count').text($.cookie('unclaimed'));
        }
        if ($.cookie('claimed')) {
          $('#claimed-count').text($.cookie('claimed'));
        }
        if ($.cookie('emergent')) {
          $('#emergent-count').text($.cookie('emergent'));
        }
        if ($.cookie('returned')) {
          $('#returned-count').text($.cookie('returned'));
        }
      }
      $('#'+URL.params.letter_status).addClass('active').siblings().removeClass('active');
    }

    if (URL.params && URL.params.start) {
      $('.pagination:visible li.active').removeClass('active');
      $('.pagination:visible ul li:nth-child('+URL.params.start+')').addClass('active');
    }
  };

  init();
})();
