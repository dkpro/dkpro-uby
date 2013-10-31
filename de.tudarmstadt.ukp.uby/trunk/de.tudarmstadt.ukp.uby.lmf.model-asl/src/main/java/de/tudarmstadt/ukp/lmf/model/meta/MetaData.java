package de.tudarmstadt.ukp.lmf.model.meta;

import java.util.Date;

/**
 * Meta data information for semantic axis
 * @author Yevgen Chebotar
 *
 */
public class MetaData {
	private String id;
	private Date creationDate;
	private String creationTool;
	private String version;
	private String creationProcess;
	private Boolean automatic;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * @return the creationTool
	 */
	public String getCreationTool() {
		return creationTool;
	}
	/**
	 * @param creationTool the creationTool to set
	 */
	public void setCreationTool(String creationTool) {
		this.creationTool = creationTool;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the creationProcess
	 */
	public String getCreationProcess() {
		return creationProcess;
	}
	/**
	 * @param creationProcess the creationProcess to set
	 */
	public void setCreationProcess(String creationProcess) {
		this.creationProcess = creationProcess;
	}
	/**
	 * @return the automatic
	 */
	public Boolean isAutomatic() {
		return automatic;
	}
	/**
	 * @param automatic the automatic to set
	 */
	public void setAutomatic(Boolean automatic) {
		this.automatic = automatic;
	}
	
}
