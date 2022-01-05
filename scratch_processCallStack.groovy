def processCallStack = "testing_FWK (core) Contact [CMM-HUB] UPDATE  > AO [FWK] (sub) CACHE Notification Facade"


def processCallStackArr = processCallStack.split(">")

for (i in 0..<processCallStackArr.size()) {
    if (!processCallStackArr[i].contains("[FWK] (sub) CACHE Notification")) {
        
        println processCallStackArr[i].trim()
    }
}


