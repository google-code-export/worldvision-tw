

function getUrlVars()
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}
function setSelected(target, value){
    var current = 0;
    $(target).children().each(function(){
        var attr_value = $(this).attr("value");
        if (value == attr_value){
            if (target.options)
                target.options[current].selected = true;
            else
                $(this).attr("selected", "selected");
            
		}
        current ++;
	});
}

function markSelected(target, value){
    if (!value || value== null || value == '')
        value = 'chi';
    $(target).children().each(function(){
        var attr_value = $(this).attr("name");
		if (value == attr_value){
			$(this).attr("style", "font-weight: bold; color: #EB8F00;text-decoration:none");
		}
		else
			$(this).removeAttr('style');
	});
}
var STOP_PROPAGATION = false;
function check_file_name_and_post_parameters (event, form,p) {
  var file_name = $('input[name*="myFile"]', form).val();

  if (file_name == null || file_name == ""){
      STOP_PROPAGATION = true;
      alert('請先選擇要上傳的檔案');
      return false;
  }
  else{
      for (var k in p) {
        var myInput = document.createElement("input") ;
        myInput.style.display = "none";
        myInput.setAttribute("name", k) ;
        myInput.setAttribute("value", p[k]);
        form.appendChild(myInput);
      }
      STOP_PROPAGATION = false;
      return true;
  }
}

function post_submit (form,p) {
   check_file_name_and_post_parameters (form,p);
   
}

function check_updating_letters(id, type){
    //check letter_type
    var letter_type = $('#' + id + '_letter_source_type').val();
    if (type == 'eng'){
        if (letter_type && letter_type == '請選擇'){
           alert("請選擇信件種類");
           return false;
        }
        //check country
        var country = $('#' + id + '_country').val();
        if (!country){
           alert("請輸入信件國家");
           return false;
        }
    }
    //check # of letters
    var number_of_letters = parseInt($('#' + id + '_number_of_letters').val());
    if (!number_of_letters || number_of_letters < 0 ){
       alert("請輸入信件封數");
       return false;
    }
	//id,country,note,number_of_letters,letter_source_type,return_days
	$.ajax({
	  type: "POST",
	  url: "/update_letter",
	  data: { 
		id: id, 
		country: $('#' + id + '_country').val(), 
		note: $('#' + id + '_note').val(), 
		number_of_letters: $('#' + id + '_number_of_letters').val(),
		letter_source_type: $('#' + id + '_letter_source_type').val(),
		return_days: $('#' + id + '_return_days').val()
		},
	  beforeSend: function () {
		$('#' + id + '_msg').removeClass('ajax_msg_error').html('處理中');
	  }	
	}).done(function( msg ) {
		if ($('#' + id + '_save_button')){
			$('#' + id + '_save_button').attr('value', '更新');
			$('#' + id + '_img').remove();
		}
	  	$('#' + id + '_msg').removeClass('ajax_msg_error').html('更新完成');
	}).fail(function() { 
		$('#' + id + '_msg').addClass('ajax_msg_error').html('更新失敗');
		alert("更新失敗, 請再試一次"); 
	})
}

function delete_letters_submit(form_id, checkbox_name){
    var query_string = '/delete_letter?';
    $("input." + checkbox_name).each(
        function()
        {
            if (this.checked)
            {
                query_string += ("ids[]=" + this.value + "&");
            }
    });
    $.ajax({
      url: query_string,
      success: function(){
        window.location.reload();
      }
    });
    return false;
//    form.method= "get";
//    form.action="/delete_letter?"+ query_string;
//    alert ("query:" + form.action.toString());
//    form.submit();
}

function show_loading_dialog(){
    if (!STOP_PROPAGATION){
        $('#dialog').append('<span>請稍候...</span><img src="/ajax.gif" alt="">');
        $('#dialog').dialog();
    }
}

function stop_event_propagation(e)
{
   var event = e || window.event;
   if (event.stopPropagation){
       event.stopImmediatePropagation();
   }
   else if(window.event){
      window.event.cancelBubble=true;
   }
}

function popitup(url) {
	  newwindow=window.open(url,'name','height=500,width=500');
	  if (window.focus) {newwindow.focus()}
	  return false;
}
	
