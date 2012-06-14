<#-- name of class doesn't matter -->
public class ClassName implements Serializable {

<#assign utUnCap = "${unCapitalizeFirst(ut)}"<#-- UT name with lower case first letter-->>
<#assign utCap = "${capitalizeFirst(ut)}"<#-- UT name with upper case first letter-->>

	public ${utCap}RB ${utUnCap}(${utCap}PB ${utUnCap}ParamBean)
			throws Exception {
			
		${utCap}RB ${utUnCap}Rb = new ${utCap}RB();
		
		 //TODO fill in result bean
		 return ${utUnCap}Rb;	
	}

}