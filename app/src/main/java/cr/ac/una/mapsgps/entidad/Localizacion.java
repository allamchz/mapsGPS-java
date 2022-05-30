package cr.ac.una.mapsgps.entidad;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Localizacion implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int uid;


    @ColumnInfo(name = "latitud")
    private double latitud;

    @ColumnInfo(name = "longitud")
    private double longitud;

    @ColumnInfo(name = "fecha")
    private Date fecha;

    public Localizacion(double latitud, double longitud, Date fecha) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
