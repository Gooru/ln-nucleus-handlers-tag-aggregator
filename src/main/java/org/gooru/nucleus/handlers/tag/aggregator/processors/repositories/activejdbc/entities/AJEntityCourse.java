package org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.entities;

import java.sql.SQLException;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author szgooru Created On: 05-Oct-2017
 */
@Table("course")
public class AJEntityCourse extends Model {

    private static final Logger LOGGER = LoggerFactory.getLogger(AJEntityCourse.class);

    public static final String ID = "id";
    public static final String TAXONOMY = "taxonomy";
    public static final String AGGREGATED_TAXONOMY = "aggregated_taxonomy";
    public static final String AGGREGATED_GUT_CODES = "aggregated_gut_codes";

    public static final String SELECT_COURSE =
        "SELECT id, taxonomy, aggregated_taxonomy, aggregated_gut_codes FROM course WHERE id = ?::uuid AND is_deleted = false";

    public static final String JSONB_TYPE = "jsonb";

    public void setAggregatedTaxonomy(String aggregatedTaxonomy) {
        setPGObject(AGGREGATED_TAXONOMY, JSONB_TYPE, aggregatedTaxonomy);
    }

    public void setAggregatedGutCodes(String aggregatedGutCodes) {
        setPGObject(AGGREGATED_GUT_CODES, JSONB_TYPE, aggregatedGutCodes);
    }

    private void setPGObject(String field, String type, String value) {
        PGobject pgObject = new PGobject();
        pgObject.setType(type);
        try {
            pgObject.setValue(value);
            this.set(field, pgObject);
        } catch (SQLException e) {
            LOGGER.error("Not able to set value for field: {}, type: {}, value: {}", field, type, value);
            this.errors().put(field, value);
        }
    }
}
