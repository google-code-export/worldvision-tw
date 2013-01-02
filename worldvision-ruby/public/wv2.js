(function() {
  var URL = {};

  function init() {
    // Then parse the URL
    URL = $.url.parse();

    // No letter status specified, save the count of letters in cookie
    if (! 'letter_status' in URL.params) {
      $.cookie('all', $('#all-count').text());
      $.cookie('uncliamed', $('#unclaimed-count').text());
      $.cookie('claimed', $('#claimed-count').text());
      $.cookie('emergent', $('#emergent-count').text());
      $.cookie('returned', $('#returned-count').text());
      $.cookie('hash2', '');
      $('#all').addClass('active').siblings().removeClass('active');
    } else {
      // Restore the count
      if ($.cookie('all')) {
        $('#all-count').text($.cookie('all'));
      }
      if ($.cookie('unclaimed')) {
        $('#unclaimed-count').text($.cookie('all'));
      }
      if ($.cookie('claimed')) {
        $('#claimed-count').text($.cookie('all'));
      }
      if ($.cookie('emergent')) {
        $('#emergent-count').text($.cookie('all'));
      }
      if ($.cookie('returned')) {
        $('#returned-count').text($.cookie('all'));
      }
      $('#'+URL.params.letter_status).addClass('active').siblings().removeClass('active');
    }

    if (URL.params && URL.params.start) {
      $('.pagination:visible li.active').removeClass('active');
      $('.pagination:visible ul li:nth-child('+URL.params.start+')').addClass('active');
    }
  };
})();
