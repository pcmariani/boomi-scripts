/* @data
{
  "expand": "operations,versionedRepresentations,editmeta,changelog,renderedFields",
  "self": "hello",
  "id": "60012",
  "fields": {
    "issuetype": {
      "avatarId": 10313,
      "hierarchyLevel": 0,
      "name": "Initiative",
      "self": "hello",
      "description": "",
      "id": "10020",
      "iconUrl": "hello",
      "subtask": false
    },
    "description": "14141414 - TEST MIGRATION - AM MULTI PRODUCT",
    "project": {
      "simplified": false,
      "avatarUrls": {
        "48x48": "hello",
        "24x24": "hello",
        "16x16": "hello",
        "32x32": "hello"
      },
      "name": "Account Management",
      "self": "hello",
      "id": "10043",
      "projectTypeKey": "software",
      "key": "AM"
    },
    "customfield_10055": {
      "self": "hello",
      "id": "10167",
      "value": "Peach"
    },
    "resolution": null,
    "customfield_10179": null,
    "attachment": [],
    "customfield_10209": null,
    "summary": "14141414 - TEST MIGRATION - AM MULTI PRODUCT",
    "creator": {
      "accountId": "557058:1fad6b1a-f3e1-47d3-be10-6a927f7da822",
      "avatarUrls": {
        "48x48": "hello",
        "24x24": "hello",
        "16x16": "hello",
        "32x32": "hello"
      },
      "displayName": "Kyle Kwoka",
      "accountType": "atlassian",
      "self": "hello",
      "active": true,
      "timeZone": "America/New_York"
    },
    "customfield_10180": "50005000004ICkNAAW",
    "created": "2022-03-29T13:19:06.885-0400",
    "customfield_10061": [
      {
        "self": "hello",
        "id": "10188",
        "value": "Radixx Res"
      },
      {
        "self": "hello",
        "id": "10189",
        "value": "Radixx Go"
      }
    ],
    "customfield_10162": {
      "self": "hello",
      "id": "10320",
      "value": "Minor"
    },
    "reporter": {
      "accountId": "557058:1fad6b1a-f3e1-47d3-be10-6a927f7da822",
      "avatarUrls": {
        "48x48": "hello",
        "24x24": "hello",
        "16x16": "hello",
        "32x32": "hello"
      },
      "displayName": "Kyle Kwoka",
      "accountType": "atlassian",
      "self": "hello",
      "active": true,
      "timeZone": "America/New_York"
    },
    "priority": {
      "name": "Medium",
      "self": "hello",
      "iconUrl": "hello",
      "id": "3"
    },
    "customfield_10166": {
      "self": "hello",
      "id": "10326",
      "value": "No"
    },
    "customfield_10144": "RDX-14141414",
    "customfield_10100": null,
    "customfield_10001": null,
    "customfield_10101": null,
    "customfield_10102": null,
    "customfield_10103": null,
    "customfield_10115": null,
    "assignee": null,
    "updated": "2022-04-07T10:24:33.018-0400",
    "status": {
      "name": "In Development",
      "self": "hello",
      "description": "This issue is being actively worked on at the moment by the assignee.",
      "iconUrl": "hello",
      "id": "3",
      "statusCategory": {
        "colorName": "yellow",
        "name": "In Progress",
        "self": "hello",
        "id": 4,
        "key": "indeterminate"
      }
    }
  },
  "key": "AM-1310"
}
*/
/* @props
    #DPP_Key=val
    #document.dynamic.userdefined.ddp_Key=val
*/
import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger()

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);

    def root = new JsonSlurper().parseText(is.getText())
    if ( root.fields.customfield_10061) {
      root.fields.customfield_10061 = [root.fields.customfield_10061.first()]
    }

    def outData = JsonOutput.prettyPrint(JsonOutput.toJson(root))

    is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
    dataContext.storeStream(is, props);
}

