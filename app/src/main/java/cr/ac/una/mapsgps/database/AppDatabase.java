package cr.ac.una.mapsgps.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import cr.ac.una.mapsgps.dao.LocalizacionDao;
import cr.ac.una.mapsgps.entidad.Localizacion;

@Database(entities = {Localizacion.class}, version = 1)
@TypeConverters(Converter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LocalizacionDao localizacionDao();
}