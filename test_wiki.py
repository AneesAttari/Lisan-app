import urllib.request
import json

def get_image_for_word(word):
    url = f"https://en.wikipedia.org/w/api.php?action=query&titles={word}&prop=pageimages&format=json&pithumbsize=600"
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        response = urllib.request.urlopen(req)
        data = json.loads(response.read().decode())
        pages = data['query']['pages']
        for page_id in pages:
            if 'thumbnail' in pages[page_id]:
                return pages[page_id]['thumbnail']['source']
    except Exception as e:
        pass
    return None

print(get_image_for_word("Apple"))
