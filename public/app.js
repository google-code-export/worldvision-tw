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
    $(target).children().each(function(){
        var attr_value = $(this).attr("value");
		if (value == attr_value){
			$(this).attr("selected", "selected");
		}
		else
			$(this).removeAttr('selected');
	});
}

function markSelected(target, value){
    if (!value || value== null || value == '')
        value = 'chi';
    $(target).children().each(function(){
        var attr_value = $(this).attr("name");
		if (value == attr_value){
			$(this).attr("style", "font-weight: bold; color: #EB8F00");
		}
		else
			$(this).removeAttr('style');
	});
}

function post_parameters (form,p) {
  for (var k in p) {
    var myInput = document.createElement("input") ;
	myInput.style.display = "none";
    myInput.setAttribute("name", k) ;
    myInput.setAttribute("value", p[k]);
    form.appendChild(myInput);
  }
}

function post_submit (form,p) {
   post_parameters (form,p);
   
}

function check_updating_letters(id){
    //check letter_type
    var letter_type = $('#' + id + '_letter_source_type').val();
    if (letter_type == '請選擇'){
       alert("請選擇信件種類");
       return false;
    }
    //check country
    var country = $('#' + id + '_countries').val();
    if (country == '請選擇'){
       alert("請選擇信件國家");
       return false;
    }
    //check # of letters
    var number_of_letters = parseInt($('#' + id + '_number_of_letters').val());
    if (!number_of_letters || number_of_letters < 0 ){
       alert("請輸入信件封數");
       return false;
    }
}

	
