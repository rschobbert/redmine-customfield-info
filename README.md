redmine-customfield-info
========================

A webapp which can be used to serve information on redmine custom fields.

To create a customized webapp, do the following:
* run the command 'gradlew generateConfigTemplate'
* rename the generated template file to 'redmine-databases.groovy
* modify this file according to your needs
* run the command 'gradlew war'
* deploy the generated war file from build/libs to your favorite servlet container, you probably want to rename the war file first (maybe just remove the version number)

The following parameters are mandatory:
* location
** one of the configured locations, see your redmine-databases.groovy file (or the web.xml) for possible values
* key
** the api key, this value can be found in your redmine instance. Go to 'My Account' in redmine, and find your personal REST api access key on the right side.
* type
** one of the possible redmine customfield types (look in your redmine database in the table 'custom_fields' for possible values)

Additionally the following optional parameters are supported
* reload
** Beware that the value is irrelevant, if this value is present it always signals true. So reload=false does not mean what it should.
** Tells the servlet that it should reread the cached customfields. Useful if you modified the customfields in your redmine instance.


Now test your webapp: 
call your server e.g.: http://localhost:8080/redmine-customfield-info/custom_fields?key=1234567890123456789012345678901234567890&location=production&type=TimeEntryCustomField
