function mockHideDivs(searchClass) {
	var tags = document.getElementsByTagName("div");
	var tcl = " "+searchClass+" ";
	for(i=0,j=0; i<tags.length; i++) { 
		var test = " " + tags[i].className + " ";
		if (test.indexOf(tcl) != -1) 
			tags[i].style.display = "none";
	}	
}