/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.intellect.fna.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
/**
 *
 * @author p.venugopal
 */
@Entity
@Table(name="mst_fna_customer")

/*@NamedQueries({
    @NamedQuery(name="ownerQry1",query="SELECT obj FROM CustomerDetailsVO obj where obj.fnaID=:fna_ID"),
     @NamedQuery(name="loadFNAIds",query="SELECT obj.fnaID FROM CustomerDetailsVO obj "),
     @NamedQuery(name="validFNAIds",query="SELECT obj.fnaID FROM CustomerDetailsVO obj "),
    @NamedQuery(name="custStatusQry",query="SELECT obj FROM CustomerDetailsVO obj WHERE obj.fnaID=:fna_ID"),
    @NamedQuery(name="custCategoryQry",query="SELECT a.parDesc FROM Ca840pbVO a,CustomerDetailsVO b WHERE b.custType=a.key2 AND a.key1 = 'CUST_CATEGORY' AND b.fnaID=:fna_ID AND b.brCd=:brCd ")    
})*/
public class CustomerDetailsVO {
    @Id
    @Column(name="fna_id",length=10,nullable=false)
    private String fnaID;
    
    @Column(name="CUSTOMER_TYPE",length=10,nullable=false)
    private String custType;
    @Column(name="BR_CD",length=10,nullable=false)
    private String brCd;
    
    public String getFnaID()
    {
        return fnaID;
    }
    public void getFnaID(String fnaID) {
        this.fnaID = fnaID;
    }

    @Column(name="base_no",length=10,nullable=false)
    private String baseNo;

    @Column(name="CUST_STATUS",length=10,nullable=false)
    private String custStatus;
    
    public String getBaseNo()
    {
        return baseNo;
    }
    public void setBaseNo(String baseNo) {
        this.baseNo = baseNo;
    }

    @Column(name="cif_no",length=10,nullable=false)
    private String cifNo;

    public String getCifNo()
    {
        return cifNo;
    }
    public void setCifNo(String cifNo) {
        this.cifNo = cifNo;
    }

    @Column(name="od_prospect_id",length=10,nullable=false)
    private String prospectNo;

    public String getProspectNo()
    {
        return prospectNo;
    }
    public void setProspectNo(String prospectNo) {
        this.prospectNo = prospectNo;
    }
	public String getCustStatus() {
		return custStatus;
	}
	public void setCustStatus(String custStatus) {
		this.custStatus = custStatus;
	}
	public String getCustType() {
		return custType;
	}
	public void setCustType(String custType) {
		this.custType = custType;
	}
	public String getBrCd() {
		return brCd;
	}
	public void setBrCd(String brCd) {
		this.brCd = brCd;
	}
}
