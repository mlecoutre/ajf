package ${corePolicyPackage};

import ${libBusinessPackage}.${function.name}BD;
<#list function.UTs as ut>
<#assign utCap = "${capitalizeFirst(ut)}"<#-- UT name with upper case first letter-->>
import ${libBusinessDtoPackage}.${utCap}PB;
import ${libBusinessDtoPackage}.${utCap}RB;
</#list>

<#assign FunctionName = "${capitalizeFirst(function.name)}">
public class ${FunctionName}Policy implements ${FunctionName}BD {

	<#list function.UTs as ut>
	<#assign utUnCap = "${unCapitalizeFirst(ut)}"<#-- UT name with lower case first letter-->>
	<#assign utCap = "${capitalizeFirst(ut)}"<#-- UT name with upper case first letter-->>
	public ${utCap}RB ${utUnCap}(${utCap}PB ${utUnCap}ParamBean)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	</#list>
	
	}