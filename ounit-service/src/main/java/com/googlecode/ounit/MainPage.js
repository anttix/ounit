/*
 * OUnit - an OPAQUE compliant framework for Computer Aided Testing
 *
 * Copyright (C) 2010, 2011  Antti Andreimann
 *
 * This file is part of OUnit.
 *
 * OUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OUnit.  If not, see <http://www.gnu.org/licenses/>.
 */

function createCodeEditor(area) {
	if(CodeMirror == undefined) return; // Degrade gracefully if CM is not available
	
	/* Map supported mime types from http://codemirror.net/ to file extensions */
	var modes = {
	  "text/x-java"             : "\.(java)$",
	  "text/x-csrc"             : "\.(h|c)$",
	  "text/x-c++src"           : "\.(hh|cc|hxx|cxx|hpp|cpp|hs|cs)$",
	  "text/css"                : "\.(css)$",
	  "text/x-diff"             : "\.(patch|diff)$",
	  "text/x-haskell"          : "\.(hs|lhs)$",
	  "text/x-stsrc"            : "\.(sm|sll)$",
	  "text/x-rst"              : "\.(rest)$",               // FIXME: Restructured text file ext? 
	  "text/x-plsql"            : "\.(sql|psql)$",
	  "text/html"               : "\.(html|xhtml|htm|tpl)$",
	  "text/javascript"         : "\.(js)$",
	  "application/json"        : "\.(json)$",
	  "application/x-httpd-php" : "\.(php|inc)$",
	  "text/x-python"           : "\.(py)$",
	  "text/stex"               : "\.(tex|ltx)$",
	  "application/xml"         : "\.(xml|xsl)$"
	};

	var indentUnits = {
	  "text/html"               : 2,
      "text/javascript"         : 2,
      "application/json"        : 2,
      "application/xml"         : 2
    };

	var options = {
	  lineNumbers: true,
	  matchBrackets: true,
	  indentUnit: 4,
	  tabMode: 'shift',
	  onCursorActivity: function(editor) {
	    if('hlLine' in editor) {
	      editor.setLineClass(editor.hlLine, null);
	    }
	    editor.hlLine = editor.setLineClass(editor.getCursor().line, "activeline");
	  }
	};

	if("title" in area) {
	  for(var mode in modes) {
		  var re = new RegExp(modes[mode], "i");
		  if(re.exec(area.title)) {
		    options["mode"] = mode;
		    if(mode in indentUnits)
		      options["indentUnit"] = indentUnits[mode];
		    break;
		  }
	  }
	}
	
	if(typeof area.attributes["readonly"] != "undefined") {
		options["readOnly"] = true;
	}
	
	area.cm = CodeMirror.fromTextArea(area, options);
}

function doJQuery() {

var was_error = false;

if(typeof jQuery == 'undefined') {
	window.setTimeout(doJQuery, 500);
	was_error = true;
	return;
}

(function($) {

  function ouActivateTabs() {

    $(".ou-tabpanel").each(function(index, element) {
      var $tabs = $(element).tabs({
		tabTemplate: "<li><a class='close' href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
        ajaxOptions: {
          error: function(xhr, status, index, anchor) {
            $(anchor.hash).html("Error loading tab contents.");
          }
        },
        add: function(event, ui) {
          $tabs.urlMap[ui.panel.id] = $tabs.lastUrl;
          $tabs.lastUrl = null;
          $tabs.tabs('select', ui.index);
        },
        remove: function(event, ui) {
          $tabs.urlMap[ui.panel.id] = null;          
        },
        show: function(event, ui) {
          $("textarea", ui.panel).each(function(i, e) {
            if("cm" in e) {
              e.cm.refresh();
        	}
          });
        }
      });

      /* Find tab URL-s */
      $tabs.urlMap = {};
      $(".ui-tabs-nav li a", $tabs).each(function(i, a){
        $tabs.urlMap[i] = a.href;
      });
      
      /* Attach tab loading function to project navigator tree elements */
      $(".ou-explorer-tree a", element).click(function(event) {
        event.preventDefault();
        var url = this.href;
        var tabId = null;
        for(var prop in $tabs.urlMap) {
          if($tabs.urlMap[prop] == url) {
            tabId = prop;
            break;
          }
        }
        if(tabId != null) {
          if(parseInt(tabId) >= 0) tabId = parseInt(tabId);
          $tabs.tabs("select", tabId);
        } else {
          $tabs.lastUrl = url;
          $tabs.tabs("add", url, $(this).text() );
        }
      });
      
      /* Implement closable tabs */    
  	  $("span.ui-icon-close", $tabs).live( "click", function() {
  		var index = $("li", $tabs ).index( $( this ).parent() );
  		$tabs.tabs("remove", index);
  	  });
    });
  }

  $(document).ready(function() {
  
    $("textarea.ou-codeeditor").each(function(i, a) {
      createCodeEditor(a);
	});
    ouActivateTabs();
    
  });
  
})(jQuery);

}

doJQuery();

/* 
 * We use plain old DOM to attatch the evet here, because jQuery may not be available yet.
 * The reason is that our scripts are loaded from both:
 * HEAD and body which makes them to load in parallel and the execution order
 * is therefore not determined.
 * 
 * http://blogs.msdn.com/b/kristoffer/archive/2006/12/22/loading-javascript-files-in-parallel.aspx
 * 
 * 
 * FIXME: Bullshit?
 */

function addEvent(obj, evType, fn, useCapture){
  if (obj.addEventListener){
	obj.addEventListener(evType, fn, useCapture);
	return true;
  } else if (obj.attachEvent){
	var r = obj.attachEvent("on"+evType, fn);
    return r;
  } else {
    return false; // Too bad, failed!
  }
}

//addEvent(window, 'load', doJQuery, false);