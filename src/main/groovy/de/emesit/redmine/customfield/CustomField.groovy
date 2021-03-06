package de.emesit.redmine.customfield

import java.io.StringWriter;
import java.util.List;

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.xml.MarkupBuilder;


class CustomField {
    /*
    Currently (redmine 1.4.4) the CUSTOMFIELDS table is defined like this:
    +-----------------+--------------+------+-----+---------+----------------+
    | Field           | Type         | Null | Key | Default | Extra          |
    +-----------------+--------------+------+-----+---------+----------------+
    | id              | int(11)      | NO   | PRI | NULL    | auto_increment |
    | type            | varchar(30)  | NO   |     |         |                |
    | name            | varchar(30)  | NO   |     |         |                |
    | field_format    | varchar(30)  | NO   |     |         |                |
    | possible_values | text         | YES  |     | NULL    |                |
    | regexp          | varchar(255) | YES  |     |         |                |
    | min_length      | int(11)      | NO   |     | 0       |                |
    | max_length      | int(11)      | NO   |     | 0       |                |
    | is_required     | tinyint(1)   | NO   |     | 0       |                |
    | is_for_all      | tinyint(1)   | NO   |     | 0       |                |
    | is_filter       | tinyint(1)   | NO   |     | 0       |                |
    | position        | int(11)      | YES  |     | 1       |                |
    | searchable      | tinyint(1)   | YES  |     | 0       |                |
    | default_value   | text         | YES  |     | NULL    |                |
    | editable        | tinyint(1)   | YES  |     | 1       |                |
    | visible         | tinyint(1)   | NO   |     | 1       |                |
    | multiple        | tinyint(1)   | YES  |     | 0       |                |
    +-----------------+--------------+------+-----+---------+----------------+
     */
    
    Integer id
    String  type
    String  name
    String  fieldFormat
    String  possibleValues
    String  regexp
    Integer minLength
    Integer maxLength
    Boolean isRequired
    Boolean isForAll
    Boolean isFilter
    Integer position
    Boolean searchable
    String  defaultValue
    Boolean editable
    Boolean visible
    Boolean multiple
    
    static CustomField convertRow(def nextRow) {
        return new CustomField(
            id            :nextRow.id,
            type          :nextRow.type,
            name          :nextRow.name,
            fieldFormat   :nextRow.field_format,
            possibleValues:nextRow.possible_values,
            regexp        :nextRow.regexp,
            minLength     :nextRow.min_length,
            maxLength     :nextRow.max_length,
            isRequired    :nextRow.is_required,
            isForAll      :nextRow.is_for_all,
            isFilter      :nextRow.is_filter,
            position      :nextRow.position,
            searchable    :nextRow.searchable,
            defaultValue  :nextRow.default_value,
            editable      :nextRow.editable,
            visible       :nextRow.visible,
            multiple      :nextRow.multiple
        )
    }
    
    static List<CustomField> queryCustomFields(Sql sql, String type) {
        List<CustomField> customFields = []
        
        sql.eachRow('select * from custom_fields where type=?', [type]) { nextRow ->
            customFields << convertRow(nextRow)
        }
        
        return customFields
    }
    
    static List<CustomField> queryAllCustomFields(Sql sql) {
        List<CustomField> customFields = []
        
        sql.eachRow('select * from custom_fields') { nextRow ->
            customFields << convertRow(nextRow)
        }
        
        return customFields
    }
    
    static List<String> possibleValues(String possibleValueString) {
        List values = []
        if (possibleValueString) {
            List<String> rawTokens = possibleValueString.tokenize('\n')
            if (rawTokens && rawTokens.size() > 1) {
                for (String nextRawToken : rawTokens[1..-1]) {
                    values << nextRawToken[2..-1]
                }
            }
        }
        return values
    }
    
    static String toXml(List<CustomField> customFields, StringWriter writer = null, MarkupBuilder builder = null) {
        if (builder == null) {
            writer = new StringWriter()
            builder = new MarkupBuilder(writer)
        }
        builder.custom_fields(type:'array') {
            for (CustomField nextCustomField in customFields) {
                nextCustomField.toXml(writer, builder)
            }
        }
        writer.toString()
    }
    
    
    
    String toXml(StringWriter writer = null, MarkupBuilder builder = null) {
        if (builder == null) {
            writer = new StringWriter()
            builder = new MarkupBuilder(writer)
        }
        
        builder.custom_field(id:id, name:name) {
            'type'         ("${type}")
            'format'       ("${fieldFormat}")
            'is_required'  ("${isRequired}")
            if (fieldFormat == 'list') {
                if (multiple!=null) {
                    'multiple'     ("${multiple}")
                }
                'possible_values'() {
                    for (String nextPossibleValue : possibleValues(possibleValues)) {
                        'value'(nextPossibleValue)
                    }
                }
                
            } else if (fieldFormat == 'bool') {
                if (defaultValue!=null) {
                    'default_value'("${defaultValue}")
                }
                
            } else {
                
                'min_length'   ("${minLength}")
                'max_length'   ("${maxLength}")
                if (regexp!=null) {
                    'regexp'("${regexp}")
                }
                if (defaultValue!=null) {
                    'default_value'("${defaultValue}")
                }
            }
//            'is_for_all'       ("${isForAll}")
//            'is_filter'        ("${isFilter}")
//            'position'         ("${position}")
//            'searchable'       ("${searchable}")
//            'editable'         ("${editable}")
//            'visible'          ("${visible}")
            
        }
        
        writer.toString()
    }
}
