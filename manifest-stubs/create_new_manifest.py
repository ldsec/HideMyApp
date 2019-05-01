__author__ = 'tpham'

import sys
import random
import string
from manifest_parser import *
import copy

def update_permissions(inner_file_name, root):
    print 'Update the permission of the container'
    inner_use_permissions = get_use_permissions(inner_file_name)
    application = root.find('application')

    inner_defined_permissions = get_defined_permissions(inner_file_name)
    for defined_permission in inner_defined_permissions:
        root.append(defined_permission)

    for permission in inner_use_permissions:   
        root.append(permission)

    


def update_activities(inner_file_name, root):
    print 'Update the activities of the container'
    application = root.find('application')
    
    container_app_activity_template = None

    for comp in list(application):
        if comp.tag == 'activity': 
            container_app_activity_template = comp
            application.remove(comp)

    application = root.find('application') 
    launchModes = ['standard', 'singleTop', 'singleInstance', 'singleTask']
    counters = [0, 0, 0, 0]

    inner_activities = get_activities_wo_intents(inner_file_name)
    for inner_activity in inner_activities:
        
        conventional_activity = copy.deepcopy(inner_activity)
        #Modify the inner activity to be used in the container 
        launchMode = conventional_activity.get('{http://schemas.android.com/apk/res/android}launchMode')
        if conventional_activity.get('{http://schemas.android.com/apk/res/android}icon') != None:
            conventional_activity.attrib.pop('{http://schemas.android.com/apk/res/android}icon')
        if conventional_activity.get('{http://schemas.android.com/apk/res/android}logo') != None:
            conventional_activity.attrib.pop('{http://schemas.android.com/apk/res/android}logo')
        #conventional_activity.attrib.clear()

        conventional_activity.set('{http://schemas.android.com/apk/res/android}process', ':PluginP08')
        conventional_activity.set('{http://schemas.android.com/apk/res/android}label', '@string/stub_name_activity')

        if launchMode == None:
            launchMode = 'standard'

        launchModes_ind = launchModes.index(launchMode)
        conventional_activity.set('{http://schemas.android.com/apk/res/android}launchMode', launchMode)
        conventional_activity.set('{http://schemas.android.com/apk/res/android}name', ".stub.ActivityStub$P08$" +
                           upcase_first_letter(launchMode) + '0' + str(counters[launchModes_ind]))

        conventional_activity.set('{http://schemas.android.com/apk/res/android}theme', '@style/DroidPluginTheme')
        
        for child in list(container_app_activity_template):
            conventional_activity.append(child)
        #for child in list(inner_activity):
        #    conventional_activity.append(child)
        
        application.append(conventional_activity)

        #Create also a dialog activity
        dialog_activity = copy.deepcopy(conventional_activity)
        dialog_activity.set('{http://schemas.android.com/apk/res/android}name', ".stub.ActivityStub$Dialog$P08$" +
                           upcase_first_letter(launchMode) + '0' + str(counters[launchModes_ind]))
        dialog_activity.set('{http://schemas.android.com/apk/res/android}theme', '@style/DroidPluginThemeDialog')
        application.append(dialog_activity)
        
        counters[launchModes_ind] += 1



def update_services(inner_file_name, root):
    print 'Update the services of the container'
    application = root.find('application')
    container_app_service_template = None
    
    for comp in list(application):
        if comp.tag == 'service' and comp.get('{http://schemas.android.com/apk/res/android}name') != '.PluginManagerService': 
            container_app_service_template = comp
            application.remove(comp)

    #for each service declared in the Manifest file of the container app, declared with generic name
    inner_services = get_services_wo_intents(inner_file_name)
    counter = 0

    for inner_service in inner_services:

        new_service = copy.deepcopy(inner_service)
        if new_service.get('{http://schemas.android.com/apk/res/android}icon') != None:
            new_service.attrib.pop('{http://schemas.android.com/apk/res/android}icon')
        new_service.set('{http://schemas.android.com/apk/res/android}process', ':PluginP08')
        new_service.set('{http://schemas.android.com/apk/res/android}label', '@string/stub_name_activity')
        new_service.set('{http://schemas.android.com/apk/res/android}name', ".stub.ServiceStub$StubP0" + str(counter) + "$P00") 
        counter += 1
        for child in list(container_app_service_template):
            new_service.append(child)
        application.append(new_service)


def upcase_first_letter(s):
    return s[0].upper() + s[1:]

if __name__ == "__main__":
    inner_app_file = sys.argv[1]
    container_app_file = sys.argv[2]
    output_file = sys.argv[3]

    ns = get_namespaces(container_app_file)
    for prefix, uri in ns.iteritems():
        ET.register_namespace(prefix, uri)

    tree = ET.parse(container_app_file)
    root = tree.getroot()

    print inner_app_file

    update_permissions(inner_app_file, root)
    update_activities(inner_app_file, root)
    
    update_services(inner_app_file, root)

    tree.write(output_file)
    

