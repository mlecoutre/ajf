<#-- name of class doesn't matter -->
public class ClassName implements Serializable {

<#-- generate an UT managedBean Mehod -->
<#assign utCap = "${capitalizeFirst(ut)}"<#-- UT name with upper case first letter-->>
<#assign utUnCap = "${unCapitalizeFirst(ut)}"<#-- UT name with lower case first letter-->> 
<#assign functionNameUnCap = "${unCapitalizeFirst(functionName)}"<#-- function name with lower case first letter-->>

	/**
	 * UT method for ${utCap}
	 * 
	 */
	public void  ${utUnCap}() {

		try {
			log.debug("Start of ${utUnCap}... ");
		
			//TODO fill in Param bean
			${utCap}PB ${utUnCap}Pb= new ${utCap}PB();
			
			//Call Policy
			${functionNameUnCap}management.${utUnCap}(${utUnCap}Pb);
						

		//} catch (BusinessLayerException e) {

			//TODO manage error

		} catch(Exception e){
			log.error("Error Occured in ${utUnCap}.",e);
		}
	}
}