<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>

<script type="text/javascript">

function RealTypeOf(v) {
  if (typeof(v) == "object") {
    if (v === null) return "null";
    if (v.constructor == (new Array).constructor) return "array";
    if (v.constructor == (new Date).constructor) return "date";
    if (v.constructor == (new RegExp).constructor) return "regex";
    return "object";
  }
  return typeof(v);
}

function FormatJSON(oData, sIndent) {
    if (arguments.length < 2) {
        var sIndent = "";
    }
    var sIndentStyle = "    ";
    var sDataType = RealTypeOf(oData);
    if (sDataType == "array") {
        if (oData.length == 0) {
            return "[]";
        }
        var sHTML = "[";
    } else {
        var iCount = 0;
        $.each(oData, function() {
            iCount++;
            return;
        });
        if (iCount == 0) {
            return "{}";
        }
        var sHTML = "{";
    }
    var iCount = 0;
    $.each(oData, function(sKey, vValue) {
        if (iCount > 0) {
            sHTML += ",";
        }
        if (sDataType == "array") {
            sHTML += ("\n" + sIndent + sIndentStyle);
        } else {
            sHTML += ("\n" + sIndent + sIndentStyle + "\"" + sKey + "\"" + ": ");
        }

        switch (RealTypeOf(vValue)) {
            case "array":
            case "object":
                sHTML += FormatJSON(vValue, (sIndent + sIndentStyle));
                break;
            case "boolean":
            case "number":
                sHTML += vValue.toString();
                break;
            case "null":
                sHTML += "null";
                break;
            case "string":
                sHTML += ("\"" + vValue + "\"");
                break;
            default:
                sHTML += ("TYPEOF: " + typeof(vValue));
        }
        iCount++;
    });
    if (sDataType == "array") {
        sHTML += ("\n" + sIndent + "]");
    } else {
        sHTML += ("\n" + sIndent + "}");
    }
    return sHTML;
}
</script>

<script type="text/javascript">

function processOutput(data,outputid) {
	y = FormatJSON(data, "");
	$(outputid).val(y);
}

function callServerEndpoint(url,outputid) {
	$.getJSON(url,
	        function(data) {
				processOutput(data,outputid);
	          });
}

$(document).ready(function() {
	
    $("#test").click(function() {
    	endpoint =  $("#endpoint").val();
    	method = $("#method").val();
    	param1 = $("#param1").val();
    	value1 =  $("#value1").val();
    	param2 = $("#param2").val();
    	value2 =  $("#value2").val();
    	
    	if (param1) {
    		url = "sherpa?endpoint=" + endpoint + "&action=" + method + "&"+param1+"="+value1;
    	} else {
    		url = "sherpa?endpoint=" + endpoint + "&action=" + method;
    	}
    		
    	if (param2) {
    		url += "&"+param2+"="+value2;
    	} 
  
    	
   		callServerEndpoint(url,"#output");
    	return false;
    });
    
});
</script>

<H1> khsSherpa JSON Remote Data Service Test Page</H1>

</ br>

<b>Execute Endpoint</b>
<br />
<table border="0"/>
<tr><td>@Endpointclass <input id="endpoint" type="input" name="endpoint" size="50" /> </td></tr>
<tr><td>Method Name  <input id="method" type="input" name="action" size="50" /> </td></tr> 
<tr><td>Parameter 1<input id="param1" type="input" name="param1" size="50" /> &nbsp; Value 1 <input id="value1" type="input" name="value1" size="50" /> </td></tr>
<tr><td>Parameter 2<input id="param2" type="input" name="param2" size="50" /> &nbsp; Value 2 <input id="value2" type="input" name="value2" size="50" /> </td></tr>
<tr><td><input id="test" type="submit" name="submit"/></td></tr>
</table>
<br />
JSON Results
<br >
<textarea id="output" cols="80" rows="8"></textarea>
<br />

 













