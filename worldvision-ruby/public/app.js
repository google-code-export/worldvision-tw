var WV = {
  init: function wv_init() {
    // XXX: you could use hashchange/route instead of url parsing.

    var type = this.getUrlVars()['type'] || 'chi';
    this.markSelected($('#tans_select'), type);

    var self = this;
    $('form').submit(function submit() {
      self.showLoadingDialog();
    });

    var hash_key = this.getUrlVars()['hash_key'];
    if (hash_key) {
      this.downloadFile(hash_key);
    }
  },

  downloadFile: function wv_downloadFile(hash_key) {
    if (hash_key) {
      document.location.href =
          'http://www.worldvision-tw.appspot.com/serve?blob-key=' + hash_key;
    }
  },

  getUrlVars: function wv_getUrlVars() {
    var vars = [], hash;
    var href = window.location.href;
    var hashes = href.slice(href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
  },

  check: function wv_check(id) {
    var ele = document.getElementById(id);

    // XXX: Use === when compare to '';
    if (ele && ele.value === '') {
      alert('請輸入退件原因');
      this.STOP_PROPAGATION = true;
      return false;
    } else {
      this.STOP_PROPAGATION = false;
      return true;
    }
  },

  setSelected: function wv_setSelected(target, value) {
    var current = 0;
    $(target).children().each(function iterator() {
      var attr_value = $(this).attr('value');
      if (value == attr_value) {
        if (target.options)
          target.options[current].selected = true;
        else
          $(this).attr('selected', 'selected');
      }
      current++;
      });
  },

  markSelected: function wv_markSelected(target, value) {
    if (!value || value == null || value === '')
      value = 'chi';

    $(target).children().each(function iterator() {
      var attr_value = $(this).attr('name');
      if (value == attr_value) {
        $(this).attr('style',
            'font-weight: bold; color: #EB8F00;text-decoration:none');
      } else {
        $(this).removeAttr('style');
      }
    });
  },


  STOP_PROPAGATION: false,

  checkFilenameAndPostParameters: function wv_checkfile(event, form, p) {
    var file_name = $('input[name*="myFile"]', form).val();

    if (file_name == null || file_name == '') {
      this.STOP_PROPAGATION = true;
      alert('請先選擇要上傳的檔案');
      return false;
    } else {
      for (var k in p) {
        var myInput = document.createElement('input');
        myInput.style.display = 'none';
        myInput.setAttribute('name', k);
        myInput.setAttribute('value', p[k]);
        form.appendChild(myInput);
      }
      this.STOP_PROPAGATION = false;
      return true;
    }
  },

  postSubmit: function wv_postSubmit(form, p) {
   check_file_name_and_post_parameters(form, p);
  },

  checkUpdatingLetters: function wv_checkUpdatingLetters(id, type) {
    //check letter_type
    var letter_type = $('#' + id + '_letter_source_type').val();
    if (type == 'eng') {
      if (letter_type && letter_type == '請選擇') {
        alert('請選擇信件種類');
        return false;
      }
      //check country
      var country = $('#' + id + '_country').val();
      if (!country) {
        alert('請輸入信件國家');
        return false;
      }
    }
    //check # of letters
    var number_of_letters = parseInt($('#' + id + '_number_of_letters').val());
    if (!number_of_letters || number_of_letters < 0) {
      alert('請輸入信件封數');
      return false;
    }
    //id,country,note,number_of_letters,letter_source_type,return_days
    $.ajax({
      type: 'POST',
      url: '/update_letter',
      data: {
        id: id,
        country: $('#' + id + '_country').val(),
        note: $('#' + id + '_note').val(),
        number_of_letters: $('#' + id + '_number_of_letters').val(),
        letter_source_type: $('#' + id + '_letter_source_type').val(),
        return_days: $('#' + id + '_return_days').val()
      },
      beforeSend: function onbeforesend() {
        $('#' + id + '_msg').removeClass('ajax_msg_error').html('處理中');
      }
    }).done(function onsuccess(msg) {
      if ($('#' + id + '_save_button')) {
        $('#' + id + '_save_button').attr('value', '更新');
        $('#' + id + '_img').remove();
      }
      $('#' + id + '_msg').removeClass('ajax_msg_error').html('更新完成');
    }).fail(function onerror() {
      $('#' + id + '_msg').addClass('ajax_msg_error').html('更新失敗');
      alert('更新失敗, 請再試一次');
    });
  },

  deleteLetttersSubmit: function wv_dls(form_id, checkbox_name) {
    var query_string = '/delete_letter?';
    $('input.' + checkbox_name).each(function iterator() {
      if (this.checked) {
        query_string += ('ids[]=' + this.value + '&');
      }
    });
    $.ajax({
      url: query_string,
      success: function onsuccess() {
        window.location.reload();
      }
    });
    return false;
//    form.method= "get";
//    form.action="/delete_letter?"+ query_string;
//    alert ("query:" + form.action.toString());
//    form.submit();
  },

  showLoadingDialog: function wv_showLoadingDialog() {
    if (!this.STOP_PROPAGATION) {
      $('#dialog').append('<span>請稍候...</span><img src="/ajax.gif" alt="">');
      $('#dialog').dialog();
    }
  },

  stopEventPropagation: function wv_stopEventPropagation(evt) {
    var event = evt || window.event;
    if (event.stopPropagation) {
       event.stopImmediatePropagation();
    } else if (window.event) {
      window.event.cancelBubble = true;
    }
  },

  claimLetter: function wv_claimLetter(id, account_id) {
    window.location.href =
        '/claim_letter?id=' + id + '&account_id=' + account_id;
  }
};

$(document).ready(function() {
  WV.init();
});

