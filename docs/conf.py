from docs_conf.conf import *
 
branch = 'jakarta'
master_doc = 'index'
 
linkcheck_ignore = [
    'http://localhost',
    'http://ejbca:8080/ejbca/publicweb/cmp/cmpRA',
]
 
exclude_patterns = [
    '.tox'
]
 
intersphinx_mapping = {}
 
html_last_updated_fmt = '%d-%b-%y %H:%M'
 
def setup(app):
    app.add_css_file("css/ribbon.css")