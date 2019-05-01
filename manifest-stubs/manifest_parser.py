__author__ = 'tpham'

import xml.etree.ElementTree as ET
from cStringIO import StringIO

def get_namespaces(file_name):
    xml = None
    namespaces = {}

    xmlin = open(file_name).read()
    for event, elem in ET.iterparse(StringIO(xmlin), ('start', 'start-ns')):
        if event == 'start-ns':
            if elem[0] in namespaces and namespaces[elem[0]] != elem[1]:
                raise KeyError("Duplicate prefix with different URI found.")

            namespaces[str(elem[0])] = elem[1]

        elif event == 'start':
            if xml is None:
                break

    return namespaces

#Get the attributes declared right below (inside) the application tag: name, theme, ...
def get_application_attributes(file_name):
    tree = ET.parse(file_name)
    root = tree.getroot()
    application = root.find('application')
    return application.attrib

#Get all the attributes and components declared inside the application tag
def get_application(file_name):
    tree = ET.parse(file_name)
    root = tree.getroot()
    application = root.find('application')
    return application

#Get the package name declared in the Manifest file
def get_pkg_name(file_name):
    tree = ET.parse(file_name)
    root = tree.getroot()
    return root.get('package')

def get_defined_permissions(file_name):
    tree = ET.parse(file_name)
    root = tree.getroot()
    permissions = root.findall('permission')
    return permissions


#Get all permissions declared in the Manifest file
def get_use_permissions(file_name):
    tree = ET.parse(file_name)
    root = tree.getroot()
    permissions = root.findall('uses-permission')
    return permissions

def get_activities(file_name):
    application = get_application(file_name)
    activities = []
    for comp in list(application):
        if comp.tag == 'activity':
            activities.append(comp)
    return activities

def get_activities_wo_intents(file_name):
    application = get_application(file_name)
    activities = []
    for comp in list(application):
        if comp.tag == 'activity':
            for child in list(comp):
                comp.remove(child)
            activities.append(comp)
    return activities

def get_services(file_name):
    application = get_application(file_name)
    services = []
    for comp in list(application):
        if comp.tag == 'service':
            services.append(comp)
    return services

def get_services_wo_intents(file_name):
    application = get_application(file_name)
    services = []
    for comp in list(application):
        if comp.tag == 'service':
            for child in list(comp):
                comp.remove(child)
            services.append(comp)
    return services
