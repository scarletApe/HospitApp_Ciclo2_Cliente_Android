package entities;

/**
 * Created by maritza on 26/05/15.
 */
public class Report {

    private Long idReport;
    private String description;
    private String medicine;
    private String indications;
    private String patientNss;
    private Appointment idAppointment;

    public Report() {
    }

    public Report(Long idReport) {
        this.idReport = idReport;
    }

    public Report(Long idReport, String patientNss) {
        this.idReport = idReport;
        this.patientNss = patientNss;
    }

    public Long getIdReport() {
        return idReport;
    }

    public void setIdReport(Long idReport) {
        this.idReport = idReport;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getIndications() {
        return indications;
    }

    public void setIndications(String indications) {
        this.indications = indications;
    }

    public String getPatientNss() {
        return patientNss;
    }

    public void setPatientNss(String patientNss) {
        this.patientNss = patientNss;
    }

    public Appointment getIdAppointment() {
        return idAppointment;
    }

    public void setIdAppointment(Appointment idAppointment) {
        this.idAppointment = idAppointment;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idReport != null ? idReport.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Report)) {
            return false;
        }
        Report other = (Report) object;
        if ((this.idReport == null && other.idReport != null) || (this.idReport != null && !this.idReport.equals(other.idReport))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "multixsoft.hospitapp.entities.Report[ idReport=" + idReport + " ]";
    }
}
