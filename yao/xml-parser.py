from urllib2 import urlopen
from xml.etree.ElementTree import parse, fromstring
import json
# Download the RSS feed and parse it
# u = urlopen('http://planet.python.org/rss20.xml')

doc = parse('/Users/Frank/Downloads/2.3/matrPlant.owl')
# doc = parse(u)
# doc = xml_text
root = doc.getroot()

concept_dictionary = dict()

for child in root:
    if child.tag.endswith('Class'):
        concept = str()
        for attr in child.attrib:
            if attr.endswith('about'):
                concept2 = child.attrib[attr]
                if concept2.startswith('#'):
                    concept = concept2[1:]
        # print(concept)
        subclass_of = []
        for sub_child in child:
            if sub_child.tag.endswith('subClassOf'):
                for attr in sub_child.attrib:
                    if attr.endswith('resource'):
                        parts = sub_child.attrib[attr].split('#')
                        if parts is not None and len(parts) > 0:
                            subclass_of.append(parts[-1])
        # print(subclass_of)
        concept_dictionary[concept] = subclass_of

json_data = json.dumps(concept_dictionary, indent=4)
print(json_data)
    # print(child)

    # Extract and output tags of interest
    # for item in doc.iterfind('channel/item'):
    #     title = item.findtext('title')
    #     date = item.findtext('pubDate')
    #     link = item.findtext('link')

    #     print(title)
    #     print(date)
    #     print(link)
    #     print()
