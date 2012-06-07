package ${libBusinessPackage};

<#list function.UTs as ut>
<#assign utCap = "${capitalizeFirst(ut)}"<#-- UT name with upper case first letter-->>
import ${libDtoPackage}.${utCap}PB;
import ${libDtoPackage}.${utCap}RB;
</#list>

public interface ${function.name}BD  {

	
	<#list function.UTs as ut>
		<#assign utCap = "${capitalizeFirst(ut)}"<#-- UT name with upper case first letter-->>
		<#assign utUnCap = "${unCapitalizeFirst(ut)}"<#-- UT name with lower case first letter-->> 
	/**
	 * Business Service for UT : ${ut}
	 * @param ${utUnCap}
	 *
	 */ 
	${utCap}RB ${utUnCap}(
			${utCap}PB ${utUnCap}ParamBean)
			throws Exception;
			
			
	</#list>

}
