const popup = new Notification();
async function loadProductData() {
    const response = await fetch("LoadProductData");

    if (response.ok) {
        const responseJson = await response.json();
        if (responseJson.status) {
            // Call the separate loader functions
            loadCategory(responseJson.categoryList);
            loadColor(responseJson.colorList);
        } else {
            popup.error({
                message: responseJson.message
            });
        }
    } else {
        popup.error({
            message: "Something went wrong"
        });
    }
}

// Load categories into select box
function loadCategory(categoryList) {
    const select = document.getElementById("productCategory");
    
    // Clear existing options
    select.innerHTML = "";
    
    // Add default option
    const defaultOption = document.createElement("option");
    defaultOption.value = "0";
    defaultOption.innerHTML = "Select a category";
    select.appendChild(defaultOption);
    
    // Add categories from list
    categoryList.forEach(category => {
        const option = document.createElement("option");
        option.value = category.id;
        option.innerHTML = category.name;
        select.appendChild(option);
    });
    
    // Add "Add New" option
    const addNewOption = document.createElement("option");
    addNewOption.value = "add";
    addNewOption.innerHTML = "+ Add New Category";
    select.appendChild(addNewOption);
    
    // Add change event handler
    select.addEventListener('change', function() {
        if (this.value === 'add') {
            document.getElementById('newCategoryForm').style.display = 'block';
            document.getElementById('newCategory').focus();
        } else {
            document.getElementById('newCategoryForm').style.display = 'none';
        }
    });
}

// Load colors into select box
function loadColor(colorList) {
    const select = document.getElementById("productColor");
    
    // Clear existing options
    select.innerHTML = "";
    
    // Add default option
    const defaultOption = document.createElement("option");
    defaultOption.value = "0";
    defaultOption.innerHTML = "Select a color";
    select.appendChild(defaultOption);
    
    // Add colors from list
    colorList.forEach(color => {
        const option = document.createElement("option");
        option.value = color.id;
        option.innerHTML = color.name;
        select.appendChild(option);
    });
    
    // Add "Add New" option
    const addNewOption = document.createElement("option");
    addNewOption.value = "add";
    addNewOption.innerHTML = "+ Add New Color";
    select.appendChild(addNewOption);
    
    // Add change event handler
    select.addEventListener('change', function() {
        if (this.value === 'add') {
            document.getElementById('newColorForm').style.display = 'block';
            document.getElementById('newColorName').focus();
        } else {
            document.getElementById('newColorForm').style.display = 'none';
        }
    });
}

async function addNewCategory() {
//    console.log("ok");

    const newCategory = document.getElementById("newCategory").value;
//    console.log(newCategory);

    const categoryObject = {

        newCategory: newCategory
    };

    const categoryJson = JSON.stringify(categoryObject);

    const response = await fetch(
            "AddNewCategory",
            {
                method: "POST",
                body: categoryJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
//        console.log("ok");
        const responseJosn = await response.json();

        if (responseJosn.status) {

            loadProductData();

            popup.success({
                message: responseJosn.message
            });
//            window.location.reload();

            document.getElementById("pm").click();

        } else {
//                console.log(responseJosn.message);
            popup.error({
                message: responseJosn.message
            });

        }

    } else {

        popup.error({
            message: "Somthing went wrong"
        });
    }



}

async function addNewColor() {
    //    console.log("ok");

    const newColor = document.getElementById("newColorName").value;


    const colorObject = {

        newColor: newColor
    };

    const colorJson = JSON.stringify(colorObject);

    const response = await fetch(
            "AddNewColor",
            {
                method: "POST",
                body: colorJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
//        console.log("ok");
        const responseJosn = await response.json();

        if (responseJosn.status) {

            loadProductData();
            popup.success({
                message: responseJosn.message
            });
//            window.location.reload();
            document.getElementById("pm").click();


        } else {
//                console.log(responseJosn.message);
            popup.error({
                message: responseJosn.message
            });

        }

    } else {

        popup.error({
            message: "Somthing went wrong"
        });
    }

}

async function saveProduct() {
//    console.log("ok");

    const categoryId = document.getElementById("productCategory").value;
    const colorId = document.getElementById("productColor").value;
    const productName = document.getElementById("productName").value;
    const productDescription = document.getElementById("productDescription").value;

    const image1 = document.getElementById("fileInput1").files[0];
    const image2 = document.getElementById("fileInput2").files[0];
    const image3 = document.getElementById("fileInput3").files[0];
    const image4 = document.getElementById("fileInput4").files[0];
    const image5 = document.getElementById("fileInput5").files[0];
    const image6 = document.getElementById("fileInput6").files[0];

    const form = new FormData();
    form.append("categoryId", categoryId);
    form.append("colorId", colorId);
    form.append("productName", productName);
    form.append("productDescription", productDescription);
    form.append("image1", image1);
    form.append("image2", image2);
    form.append("image3", image3);
    form.append("image4", image4);
    form.append("image5", image5);
    form.append("image6", image6);

    const response = await fetch(
            "SaveProduct",
            {
                method: "POST",
                body: form
            }
    );

    if (response.ok) {

        const json = await response.json();

        if (json.status) {//true
            popup.success({
                message: json.message
            });
            updateProductPreview();
            document.getElementById("pm").click();

        } else {//false

            popup.error({
                message: json.message
            });
        }
    }
}

// Function to update product preview
function updateProductPreview() {
    // Get form values
    const productName = document.getElementById('productName').value || 'New Tea Product';
    const productCategory = document.getElementById('productCategory');
    const productColor = document.getElementById('productColor');
    const productDescription = document.getElementById('productDescription').value || 'Product description will appear here...';

    // Get selected category and color text
    const selectedCategory = productCategory.options[productCategory.selectedIndex];
    const selectedColor = productColor.options[productColor.selectedIndex];

    const categoryText = (selectedCategory && selectedCategory.value !== '0' && selectedCategory.value !== 'add')
            ? selectedCategory.text : '-';
    const colorText = (selectedColor && selectedColor.value !== '0' && selectedColor.value !== 'add')
            ? selectedColor.text : '-';

    // Get the first uploaded image for the main preview
    const fileInput1 = document.getElementById('fileInput1');
    let mainImageUrl = null;

    if (fileInput1 && fileInput1.files && fileInput1.files[0]) {
        const file = fileInput1.files[0];
        const reader = new FileReader();

        reader.onload = function (e) {
            const imageUrl = e.target.result;

            // Update the product card image
            const productCard = document.querySelector('[style*="height: 200px"][style*="background: linear-gradient"]');
            if (productCard) {
                productCard.style.background = `url(${imageUrl}) center/cover`;
                productCard.innerHTML = ''; // Remove the tea cup icon
            }
        };

        reader.readAsDataURL(file);
    } else {
        // Reset to default if no image
        const productCard = document.querySelector('[style*="height: 200px"]');
        if (productCard) {
            productCard.style.background = 'linear-gradient(to bottom, #e8f5e9, #c8e6c9)';
            productCard.innerHTML = '<i class="fas fa-mug-hot" style="font-size: 80px; color: #4caf50;"></i>';
        }
    }

    // Update product card title
    const cardTitle = document.querySelector('[style*="font-size: 20px"]');
    if (cardTitle) {
        cardTitle.textContent = productName;
    }

    // Update product card description
    const cardDescription = cardTitle?.nextElementSibling;
    if (cardDescription) {
        cardDescription.textContent = productDescription.length > 100
                ? productDescription.substring(0, 100) + '...'
                : productDescription;
    }

    // Update product details table
    const detailsTable = document.querySelector('table');
    if (detailsTable) {
        const rows = detailsTable.querySelectorAll('tr');

        // Update Name
        if (rows[0]) {
            rows[0].cells[1].textContent = productName;
        }

        // Update Category
        if (rows[1]) {
            rows[1].cells[1].textContent = categoryText;
        }

        // Update Color
        if (rows[2]) {
            rows[2].cells[1].textContent = colorText;
        }

        // Update Description
        if (rows[3]) {
            rows[3].cells[1].textContent = productDescription || 'Description will appear here...';
            rows[3].cells[1].style.color = productDescription ? 'var(--text-dark)' : 'var(--text-medium)';
        }
    }
}

// Add event listeners for real-time preview updates
document.addEventListener('DOMContentLoaded', function () {
    // Add event listeners for real-time preview updates
    const productName = document.getElementById('productName');
    const productCategory = document.getElementById('productCategory');
    const productColor = document.getElementById('productColor');
    const productDescription = document.getElementById('productDescription');
    const fileInput1 = document.getElementById('fileInput1');

    if (productName) {
        productName.addEventListener('input', updateProductPreview);
    }

    if (productCategory) {
        productCategory.addEventListener('change', function () {
            // Show/hide new category form
            const newCategoryForm = document.getElementById('newCategoryForm');
            if (this.value === 'add') {
                newCategoryForm.style.display = 'block';
                document.getElementById('newCategory').focus();
            } else {
                newCategoryForm.style.display = 'none';
                updateProductPreview();
            }
        });
    }

    if (productColor) {
        productColor.addEventListener('change', function () {
            // Show/hide new color form
            const newColorForm = document.getElementById('newColorForm');
            if (this.value === 'add') {
                newColorForm.style.display = 'block';
                document.getElementById('newColorName').focus();
            } else {
                newColorForm.style.display = 'none';
                updateProductPreview();
            }
        });
    }

    if (productDescription) {
        productDescription.addEventListener('input', updateProductPreview);
    }

    if (fileInput1) {
        fileInput1.addEventListener('change', updateProductPreview);
    }

    // Initialize preview
    updateProductPreview();
});

// Call this function after successfully adding new category or color to update preview
function refreshPreviewAfterCategoryChange() {
    setTimeout(updateProductPreview, 100); // Small delay to ensure DOM is updated
}

// Call this function after successfully adding new color to update preview
function refreshPreviewAfterColorChange() {
    setTimeout(updateProductPreview, 100); // Small delay to ensure DOM is updated
}

