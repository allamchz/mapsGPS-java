package cr.ac.una.mapsgps.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import cr.ac.una.mapsgps.entidad.Localizacion;

@Dao
public interface LocalizacionDao {

    @Query("SELECT * FROM localizacion")
    List<Localizacion> getAll();

    @Insert
    void insert(Localizacion localizacion);

    @Delete
    void delete(Localizacion localizacion);

    @Update
    void update(Localizacion localizacion);
}
