import os
import json
from google import genai
from PIL import Image, ImageDraw, ImageFont

def generate_illustration(client: genai.Client, word: str, part_of_speech: str, definition: str) -> Image.Image:
    prompt = f"simple flat vector icon illustration of a {word}, like a modern language-learning app's vocabulary icons - bold clean shapes, soft minimal shading, no complex gradients, no photorealism, no textures. Use a small, vibrant, limited color palette (2-4 flat colors). The object sits isolated on a plain white background - no scene, no room, no environment, no cast shadow on the backdrop. Square 1:1 composition, subject centered and filling most of the frame."
    
    # Example logic assuming a generate_image function exists in the genai SDK for the chosen model
    # response = client.models.generate_image(model='imagen-3.0-generate-001', prompt=prompt)
    # image = Image.open(BytesIO(response.generated_images[0].image.image_bytes))
    # return image
    print(f"Generating image for {word}...")
    # Placeholder for actual generation
    img = Image.new('RGB', (600, 600), color=(255, 255, 255))
    return img

def add_word_label(image: Image.Image, word: str) -> Image.Image:
    draw = ImageDraw.Draw(image)
    try:
        font = ImageFont.truetype("arial.ttf", 40)
    except IOError:
        font = ImageFont.load_default()
    
    text_bbox = draw.textbbox((0, 0), word, font=font)
    text_width = text_bbox[2] - text_bbox[0]
    text_height = text_bbox[3] - text_bbox[1]
    
    x = (image.width - text_width) / 2
    y = image.height - text_height - 20
    
    draw.text((x, y), word, fill="black", font=font)
    return image

if __name__ == "__main__":
    if not os.path.exists('flashcards'):
        os.makedirs('flashcards')
        
    api_key = os.environ.get("GEMINI_API_KEY")
    if not api_key:
        print("Please set GEMINI_API_KEY environment variable.")
        exit(1)
        
    client = genai.Client(api_key=api_key)
    
    if not os.path.exists('words.json'):
        print("words.json not found. Creating a sample words_sample.json...")
        with open('words_sample.json', 'w') as f:
            json.dump([
                {"word": "apple", "part_of_speech": "noun", "definition": "a round fruit with red or green skin and a whitish interior"}
            ], f, indent=2)
        print("Please create words.json based on words_sample.json and run again.")
        exit(1)
        
    with open('words.json', 'r') as f:
        words = json.load(f)
        
    for item in words:
        word = item['word']
        print(f"Processing {word}...")
        img = generate_illustration(client, word, item.get('part_of_speech', ''), item.get('definition', ''))
        img_with_text = add_word_label(img, word)
        img_with_text.save(f"flashcards/{word}.png")
    
    print("Done! Images saved to ./flashcards/")
