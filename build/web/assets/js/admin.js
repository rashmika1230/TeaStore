// Tab switching functionality
document.addEventListener('DOMContentLoaded', function () {
    // Activate dashboard by default
    switchTab('dashboard');

    // Add event listeners to tab links
    document.querySelectorAll('.nav-item[data-tab]').forEach(item => {
        item.addEventListener('click', function () {
            const tabId = this.getAttribute('data-tab');
            switchTab(tabId);
        });
    });

    function switchTab(tabId) {
        // Hide all content areas
        document.querySelectorAll('.content-area').forEach(area => {
            area.classList.remove('active');
        });

        // Show the selected content area
        document.getElementById(tabId).classList.add('active');

        // Update active nav item
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });

        // Find the nav item with matching data-tab and activate it
        document.querySelector(`.nav-item[data-tab="${tabId}"]`).classList.add('active');
    }

    // Highlight active nav item
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', function () {
            if (this.getAttribute('data-tab')) {
                navItems.forEach(i => i.classList.remove('active'));
                this.classList.add('active');
            }
        });
    });

    // Add hover effect to stat cards
    const statCards = document.querySelectorAll('.stat-card');
    statCards.forEach(card => {
        card.addEventListener('mouseenter', function () {
            this.style.transform = 'translateY(-5px)';
        });
        card.addEventListener('mouseleave', function () {
            this.style.transform = 'translateY(0)';
        });
    });

    // Add animation to action buttons
    const actionButtons = document.querySelectorAll('.action-btn');
    actionButtons.forEach(btn => {
        btn.addEventListener('mouseenter', function () {
            this.style.transform = 'scale(1.1)';
        });
        btn.addEventListener('mouseleave', function () {
            this.style.transform = 'scale(1)';
        });
    });

    // Form functionality
    // Toggle new category form
    const categorySelect = document.getElementById('productCategory');
    const newCategoryForm = document.getElementById('newCategoryForm');

    categorySelect.addEventListener('change', function () {
        if (this.value === 'add') {
            newCategoryForm.style.display = 'block';
        } else {
            newCategoryForm.style.display = 'none';
        }
    });

    // Toggle new color form
    const colorSelect = document.getElementById('productColor');
    const newColorForm = document.getElementById('newColorForm');

    colorSelect.addEventListener('change', function () {
        if (this.value === 'add') {
            newColorForm.style.display = 'block';
        } else {
            newColorForm.style.display = 'none';
        }
    });

    // Add new category
//            document.getElementById('addCategoryBtn').addEventListener('click', function() {
//                const newCategory = document.getElementById('newCategory').value;
//                if (newCategory.trim() === '') return;
//                
//                // Add to select options
//                const option = document.createElement('option');
//                option.value = newCategory.toLowerCase().replace(/\s+/g, '-');
//                option.textContent = newCategory;
//                categorySelect.insertBefore(option, categorySelect.lastChild);
//                
//                // Reset and hide form
//                document.getElementById('newCategory').value = '';
//                newCategoryForm.style.display = 'none';
//                categorySelect.value = option.value;
//                
//                // Add to chips
//                const chip = document.createElement('div');
//                chip.className = 'category-chip';
//                chip.innerHTML = `
//                    <i class="fas fa-tag"></i> ${newCategory}
//                    <button class="delete-btn"><i class="fas fa-times"></i></button>
//                `;
//                document.querySelector('.category-chips').appendChild(chip);
//                
//                // Add delete functionality
//                chip.querySelector('.delete-btn').addEventListener('click', function() {
//                    chip.remove();
//                    // Also remove from select options
//                    categorySelect.removeChild(option);
//                });
//            });

    // Add new color
//            document.getElementById('addColorBtn').addEventListener('click', function() {
//                const colorName = document.getElementById('newColorName').value;
//                const colorValue = document.getElementById('newColorValue').value;
//                
//                if (colorName.trim() === '') return;
//                
//                // Add to select options
//                const option = document.createElement('option');
//                option.value = colorName.toLowerCase().replace(/\s+/g, '-');
//                option.textContent = colorName;
//                colorSelect.insertBefore(option, colorSelect.lastChild);
//                
//                // Reset and hide form
//                document.getElementById('newColorName').value = '';
//                newColorForm.style.display = 'none';
//                colorSelect.value = option.value;
//                
//                // Add to chips
//                const chip = document.createElement('div');
//                chip.className = 'color-chip';
//                chip.innerHTML = `
//                    <span style="background-color: ${colorValue};"></span> ${colorName}
//                    <button class="delete-btn"><i class="fas fa-times"></i></button>
//                `;
//                document.querySelector('.color-chips').appendChild(chip);
//                
//                // Add delete functionality
//                chip.querySelector('.delete-btn').addEventListener('click', function() {
//                    chip.remove();
//                    // Also remove from select options
//                    colorSelect.removeChild(option);
//                });
//            });

    // Add delete functionality to existing chips
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            this.closest('.category-chip, .color-chip').remove();
        });
    });
});


function handleFiles(files, previewId) {
    if (!files.length)
        return;

    const file = files[0];
    if (!file.type.startsWith('image/')) {
        alert('Please select an image file.');
        return;
    }

    const preview = document.getElementById(previewId);
    preview.innerHTML = '';

    const img = document.createElement('img');
    img.src = URL.createObjectURL(file);
    img.style.maxWidth = '100%';
    img.style.maxHeight = '200px';
    img.style.borderRadius = '8px';
    img.onload = () => URL.revokeObjectURL(img.src);

    preview.appendChild(img);
}

