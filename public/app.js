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
    var matched = false;
    alert ('value:' + value);
	$(target).children().each(function(){
        var attr_value = $(this).attr("value");
        alert ('attr:' + attr_value);
		if (value == attr_value){
			$(this).attr("selected", "selected");
            matched = true;
            alert('matched');
		}
		else
			$(this).attr('selected', '');
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

	
