<#-- generate an UT managedBean Mehod -->
<#assign utCap = "${capitalizeFirst(ut)}"<#-- UT name with upper case first letter-->>
<#assign utUnCap = "${unCapitalizeFirst(ut)}"<#-- UT name with lower case first letter-->> 
<#assign functionNameUnCap = "${unCapitalizeFirst(function.functionName)}"<#-- function name with lower case first letter-->>
<#assign entityNameUncap = "${unCapitalizeFirst(function.entityName)}"<#-- entity name with lower case first letter-->>
<#assign entityNameCap = "${capitalizeFirst(function.entityName)}"<#-- entity name with lower case first letter-->>


<#--imports-->
import ${function.libDTOPackage}.${utCap}PB;

<#-- name of class doesn't matter -->
public class ClassName implements Serializable {

<#-- Class Attributes to be added to managed bean for particuliar UT -->
<#-- Generated only for listUT -->
	<#if "${function.listFlag}" = "true">
		//selection values in table
		private ${entityNameCap}[] selectedItems;
		
		//data table value
		private List<${entityNameCap}> ${entityNameUncap}List;
		
		//for search person
		private ${entityNameCap} list${entityNameCap};
	</#if>
	
				
	<#-- Generated only for create UT -->
	<#if "${function.addFlag}" = "true">
	//TODO this are initial values to be modified
	private ${entityNameCap} new${entityNameCap};
	</#if>  


	<#--Need to update init method in case new entities are set-->
	@PostConstruct
	public void init() {
		<#if "${function.addFlag}" = "true">
			new${entityNameCap} = new ${entityNameCap}();
		</#if>
		
		<#if "${function.listFlag}" = "true"> 
			${entityNameUncap}List = new ArrayList<${entityNameCap}>();
			list${entityNameCap} = new ${entityNameCap}();
		</#if>
	}


	/**
	 * UT method for ${utCap}
	 * @throws Exception
	 */
	public void  ${utUnCap}() throws Exception {

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
	
	
	
	<#if "${function.listFlag}" = "true"> 
	public ${entityNameCap}[] getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(${entityNameCap}[] selectedItems) {
		this.selectedItems = selectedItems;
	}
	
	public List<${entityNameCap}> get${entityNameCap}List() {
		return ${entityNameUncap}List;
	}
	public void set${entityNameCap}List(List<${entityNameCap}> ${entityNameUncap}List){
		this.${entityNameUncap}List = ${entityNameUncap}List;
	}
	public ${entityNameCap} getList${entityNameCap}() {
		return list${entityNameCap};
	}

	public void setList${entityNameCap}(${entityNameCap} list${entityNameCap}) {
		this.list${entityNameCap} = list${entityNameCap};
	}
	
	
	</#if>
	
	<#if "${function.addFlag}" = "true"> 
	public ${entityNameCap} getNew${entityNameCap}() {
		return  new${entityNameCap};
	}
	public void setNew${entityNameCap}(${entityNameCap} new${entityNameCap}) {
		this.new${entityNameCap} = new${entityNameCap};
	}
	</#if>
}