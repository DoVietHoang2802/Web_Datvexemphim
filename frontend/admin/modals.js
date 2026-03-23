/**
 * Admin Modal Forms Utility
 * Provides reusable modal functions for admin pages
 */

// Get API_BASE from config
const MODALS_API_BASE = (typeof CONFIG !== 'undefined' && CONFIG.API_BASE)
  ? CONFIG.API_BASE
  : "http://localhost:9090/api";

/**
 * Upload image file to backend and return URL
 */
async function uploadImage(file) {
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch(`${MODALS_API_BASE}/upload/image`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('accessToken') || localStorage.getItem('token') || ''}`
    },
    body: formData
  });

  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.error || data.message || 'Upload thất bại');
  }

  return data.url;
}

export async function showMovieModal(defaults, onSubmit) {
  const modalId = "movieModal";

  // Lấy danh sách genres từ API (dùng endpoint công khai)
  let genresOptions = '<option value="">-- Chọn thể loại --</option>';
  try {
    // Dùng endpoint công khai, không cần token
    const response = await fetch(`${MODALS_API_BASE}/movies/genres`);
    if (response.ok) {
      const genres = await response.json();
      genresOptions += genres.map(g =>
        `<option value="${g.id}" ${defaults?.genreId == g.id ? 'selected' : ''}>${g.name}</option>`
      ).join('');
    }
  } catch (e) {
    console.error("Error loading genres:", e);
    // Fallback nếu không load được
    genresOptions = `
      <option value="">-- Chọn thể loại --</option>
      <option value="1">Hành động</option>
      <option value="2">Tình cảm</option>
      <option value="3">Hài hước</option>
      <option value="4">Kinh dị</option>
      <option value="5">Khoa học viễn tưởng</option>
      <option value="6">Phiêu lưu</option>
      <option value="7">Hoạt hình</option>
      <option value="8">Khác</option>
    `;
  }

  const html = `
    <div class="modal fade" id="${modalId}" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">${defaults?.id ? 'Sửa phim' : 'Thêm phim'}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <form class="admin-form" id="movieForm">
              <div class="mb-3">
                <label class="form-label">Tên phim</label>
                <input type="text" class="form-control" name="title" value="${defaults?.title ?? ''}" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Thời lượng (phút)</label>
                <input type="number" class="form-control" name="durationMinutes" value="${defaults?.durationMinutes ?? 120}" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Mô tả</label>
                <textarea class="form-control" name="description" rows="3">${defaults?.description ?? ''}</textarea>
              </div>
              <div class="mb-3">
                <label class="form-label">Hình ảnh poster</label>
                <div class="d-flex gap-2 mb-2">
                  <div class="form-check">
                    <input class="form-check-input" type="radio" name="posterType" id="posterTypeUrl" value="url" checked>
                    <label class="form-check-label" for="posterTypeUrl">Dán URL</label>
                  </div>
                  <div class="form-check">
                    <input class="form-check-input" type="radio" name="posterType" id="posterTypeFile" value="file">
                    <label class="form-check-label" for="posterTypeFile">Tải lên từ máy</label>
                  </div>
                </div>
                <div id="posterUrlSection">
                  <input type="url" class="form-control" id="posterUrlInput" name="posterUrl" value="${defaults?.posterUrl ?? ''}" placeholder="Dán URL hình ảnh (VD: https://...jpg)">
                </div>
                <div id="posterFileSection" style="display:none;">
                  <input type="file" class="form-control" id="posterFileInput" accept="image/*">
                </div>
                <div id="posterPreview" class="mt-2 text-center" style="${defaults?.posterUrl ? '' : 'display:none;'}">
                  <img id="posterPreviewImg" src="${defaults?.posterUrl ?? ''}" style="max-height:150px;max-width:100%;border-radius:8px;">
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label">URL trailer</label>
                <input type="url" class="form-control" name="trailerUrl" value="${defaults?.trailerUrl ?? ''}">
              </div>
              <div class="mb-3">
                <label class="form-label">Xếp hạng</label>
                <input type="text" class="form-control" name="rating" value="${defaults?.rating ?? ''}" placeholder="PG-13, R, etc.">
              </div>
              <div class="mb-3">
                <label class="form-label">Thể loại</label>
                <select class="form-select" name="genreId">
                  ${genresOptions}
                </select>
              </div>
              <div class="mb-3">
                <label class="form-check-label">
                  <input type="checkbox" class="form-check-input" name="active" ${defaults?.active ? 'checked' : ''}>
                  Kích hoạt
                </label>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-admin btn-outline-light" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-admin btn-admin-primary" id="submitBtn">
              <span id="submitBtnText">Lưu</span>
              <span id="submitBtnSpinner" style="display:none;" class="spinner-border spinner-border-sm"></span>
            </button>
          </div>
        </div>
      </div>
    </div>
  `;

  const container = document.body;
  const tempDiv = document.createElement('div');
  tempDiv.innerHTML = html;
  container.appendChild(tempDiv);

  const modalEl = tempDiv.querySelector(`#${modalId}`);
  const form = tempDiv.querySelector("#movieForm");
  const submitBtn = tempDiv.querySelector("#submitBtn");
  const posterUrlInput = tempDiv.querySelector("#posterUrlInput");
  const posterFileInput = tempDiv.querySelector("#posterFileInput");
  const posterPreview = tempDiv.querySelector("#posterPreview");
  const posterPreviewImg = tempDiv.querySelector("#posterPreviewImg");

  if (!modalEl || !form || !submitBtn) {
    console.error("Modal elements not found");
    tempDiv.remove();
    return;
  }

  // Toggle between URL and File input
  const posterTypeUrl = tempDiv.querySelector("#posterTypeUrl");
  const posterTypeFile = tempDiv.querySelector("#posterTypeFile");
  const posterUrlSection = tempDiv.querySelector("#posterUrlSection");
  const posterFileSection = tempDiv.querySelector("#posterFileSection");

  posterTypeUrl.addEventListener('change', () => {
    if (posterTypeUrl.checked) {
      posterUrlSection.style.display = 'block';
      posterFileSection.style.display = 'none';
      posterFileInput.value = ''; // Clear file input
    }
  });

  posterTypeFile.addEventListener('change', () => {
    if (posterTypeFile.checked) {
      posterUrlSection.style.display = 'none';
      posterFileSection.style.display = 'block';
      posterUrlInput.value = ''; // Clear URL input
      posterPreview.style.display = 'none';
    }
  });

  // Preview ảnh từ URL
  posterUrlInput.addEventListener('input', () => {
    const url = posterUrlInput.value.trim();
    if (url) {
      posterPreviewImg.src = url;
      posterPreview.style.display = 'block';
    } else {
      posterPreview.style.display = 'none';
    }
  });

  // Preview ảnh từ file
  posterFileInput.addEventListener('change', () => {
    const file = posterFileInput.files[0];
    if (!file) return;

    // Preview tạm thời
    const reader = new FileReader();
    reader.onload = (e) => {
      posterPreviewImg.src = e.target.result;
      posterPreview.style.display = 'block';
    };
    reader.readAsDataURL(file);
  });

  const bsModal = new window.bootstrap.Modal(modalEl);

  submitBtn.addEventListener("click", async (e) => {
    try {
      e.preventDefault();
      e.stopPropagation();

      // Validate basic fields
      const title = tempDiv.querySelector("[name='title']").value;
      const durationMinutes = tempDiv.querySelector("[name='durationMinutes']").value;

      if (!title || !durationMinutes) {
        alert('Vui lòng điền đầy đủ thông tin bắt buộc!');
        return;
      }

      // Disable button
      submitBtn.disabled = true;
      const submitBtnText = tempDiv.querySelector('#submitBtnText');
      const submitBtnSpinner = tempDiv.querySelector('#submitBtnSpinner');
      submitBtnText.style.display = 'none';
      submitBtnSpinner.style.display = 'inline-block';

      // Get radio value directly (not from formData)
      const posterTypeRadio = tempDiv.querySelector('input[name="posterType"]:checked');
      const selectedType = posterTypeRadio ? posterTypeRadio.value : 'url';

      let posterUrl = '';

      if (selectedType === 'file') {
        const file = posterFileInput.files[0];
        if (!file) {
          alert('Vui lòng chọn 1 hình ảnh từ máy!');
          submitBtn.disabled = false;
          submitBtnText.style.display = 'inline';
          submitBtnSpinner.style.display = 'none';
          return;
        }
        try {
          posterUrl = await uploadImage(file);
        } catch (uploadError) {
          alert('Upload ảnh thất bại: ' + uploadError.message);
          submitBtn.disabled = false;
          submitBtnText.style.display = 'inline';
          submitBtnSpinner.style.display = 'none';
          return;
        }
      } else {
        posterUrl = posterUrlInput.value.trim();
      }

      // Build data object
      const data = {
        title: title,
        durationMinutes: parseInt(durationMinutes),
        description: tempDiv.querySelector("[name='description']")?.value || '',
        posterUrl: posterUrl,
        trailerUrl: tempDiv.querySelector("[name='trailerUrl']")?.value || '',
        rating: tempDiv.querySelector("[name='rating']")?.value || '',
        genreId: tempDiv.querySelector("[name='genreId']")?.value || null,
        active: tempDiv.querySelector("[name='active']")?.checked || false
      };

      bsModal.hide();
      onSubmit(data);
    } catch (e) {
      console.error("Error in form submit:", e);
      submitBtn.disabled = false;
      const submitBtnText = tempDiv.querySelector('#submitBtnText');
      const submitBtnSpinner = tempDiv.querySelector('#submitBtnSpinner');
      if (submitBtnText) submitBtnText.style.display = 'inline';
      if (submitBtnSpinner) submitBtnSpinner.style.display = 'none';
    }
  });

  modalEl.addEventListener("hidden.bs.modal", () => {
    tempDiv.remove();
  });

  bsModal.show();
}

export function showRoomModal(defaults, onSubmit) {
  const modalId = "roomModal";
  const html = `
    <div class="modal fade" id="${modalId}" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">${defaults?.id ? 'Sửa phòng' : 'Thêm phòng'}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <form class="admin-form" id="roomForm">
              <div class="mb-3">
                <label class="form-label">Tên phòng</label>
                <input type="text" class="form-control" name="name" value="${defaults?.name ?? ''}" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Số hàng</label>
                <input type="number" class="form-control" name="totalRows" value="${defaults?.totalRows ?? 8}" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Số cột</label>
                <input type="number" class="form-control" name="totalCols" value="${defaults?.totalCols ?? 10}" required>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-admin btn-outline-light" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-admin btn-admin-primary" id="submitBtn">Lưu</button>
          </div>
        </div>
      </div>
    </div>
  `;

  showModalWithForm(html, modalId, "roomForm", "submitBtn", onSubmit);
}

export function showShowtimeModal(defaults, movies = [], rooms = [], onSubmit) {
  try {
    const modalId = "showtimeModal";
    const movieOptions = movies.map(m => `<option value="${m.id}">${m.title}</option>`).join("");
    const roomOptions = rooms.map(r => `<option value="${r.id}">${r.name}</option>`).join("");
    
    const html = `
      <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
      <div class="modal fade" id="${modalId}" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">${defaults?.id ? 'Sửa suất chiếu' : 'Thêm suất chiếu'}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <form class="admin-form" id="showtimeForm">
                <div class="mb-3">
                  <label class="form-label">Phim</label>
                  <select class="form-select" name="movieId" required>
                    <option value="">Chọn phim...</option>
                    ${movieOptions}
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Phòng</label>
                  <select class="form-select" name="roomId" required>
                    <option value="">Chọn phòng...</option>
                    ${roomOptions}
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Bắt đầu</label>
                  <input type="text" class="form-control flatpickr-datetime" id="startTimeInput" name="startTime" placeholder="Chọn ngày và giờ" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Kết thúc</label>
                  <input type="text" class="form-control flatpickr-datetime" id="endTimeInput" name="endTime" placeholder="Chọn ngày và giờ" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Giá (VND)</label>
                  <input type="number" class="form-control" name="price" value="${defaults?.price ?? 90000}" required>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-admin btn-outline-light" data-bs-dismiss="modal">Hủy</button>
              <button type="button" class="btn btn-admin btn-admin-primary" id="submitBtn">Lưu</button>
            </div>
          </div>
        </div>
      </div>
    `;

    const container = document.body;
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = html;
    container.appendChild(tempDiv);

    const modalEl = tempDiv.querySelector(`#${modalId}`);
    const form = tempDiv.querySelector("#showtimeForm");
    const submitBtn = tempDiv.querySelector("#submitBtn");
    
    if (!modalEl || !form || !submitBtn) {
      console.error("Modal elements not found");
      tempDiv.remove();
      return;
    }

    const bsModal = new window.bootstrap.Modal(modalEl);

    // Load flatpickr
    const script = document.createElement('script');
    script.src = 'https://cdn.jsdelivr.net/npm/flatpickr';
    script.onload = () => {
      const startInput = tempDiv.querySelector("#startTimeInput");
      const endInput = tempDiv.querySelector("#endTimeInput");
      
      // Initialize flatpickr for start time
      flatpickr(startInput, {
        enableTime: true,
        dateFormat: "Y-m-d H:i",
        time_24hr: true,
        minuteIncrement: 5,
        defaultValue: defaults?.startTime ? new Date(defaults.startTime) : null,
        locale: "vi"
      });
      
      // Initialize flatpickr for end time
      flatpickr(endInput, {
        enableTime: true,
        dateFormat: "Y-m-d H:i",
        time_24hr: true,
        minuteIncrement: 5,
        defaultValue: defaults?.endTime ? new Date(defaults.endTime) : null,
        locale: "vi"
      });
    };
    document.head.appendChild(script);

    submitBtn.addEventListener("click", (e) => {
      try {
        e.preventDefault();
        e.stopPropagation();
        
        if (!form.checkValidity()) {
          form.reportValidity();
          return;
        }

        const startTimeVal = tempDiv.querySelector("#startTimeInput").value;
        const endTimeVal = tempDiv.querySelector("#endTimeInput").value;
        
        if (!startTimeVal || !endTimeVal) {
          alert("Vui lòng chọn ngày và giờ bắt đầu/kết thúc");
          return;
        }

        const formData = new FormData(form);
        const data = {
          movieId: Number(formData.get("movieId")),
          roomId: Number(formData.get("roomId")),
          startTime: new Date(startTimeVal).toISOString(),
          endTime: new Date(endTimeVal).toISOString(),
          price: Number(formData.get("price"))
        };
        bsModal.hide();
        onSubmit(data);
      } catch (e) {
        console.error("Error in form submit:", e);
      }
    });

    modalEl.addEventListener("hidden.bs.modal", () => {
      tempDiv.remove();
    });

    bsModal.show();
  } catch (e) {
    console.error("Error in showShowtimeModal:", e);
  }
}

export function showConfirmModal(title, message, onConfirm, confirmText = "Xác nhận", confirmClass = "btn-admin-danger") {
  try {
    const modalId = "confirmModal";
    const html = `
      <div class="modal fade" id="${modalId}" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">${title}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <p>${message}</p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-admin btn-outline-light" data-bs-dismiss="modal">Hủy</button>
              <button type="button" class="btn btn-admin ${confirmClass}" id="confirmBtn">${confirmText}</button>
            </div>
          </div>
        </div>
      </div>
    `;

    const container = document.body;
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = html;
    container.appendChild(tempDiv);

    const modalEl = tempDiv.querySelector(`#${modalId}`);
    if (!modalEl) {
      console.error("Modal element not found");
      tempDiv.remove();
      return;
    }

    const bsModal = new window.bootstrap.Modal(modalEl);
    const confirmBtn = modalEl.querySelector("#confirmBtn");
    
    if (!confirmBtn) {
      console.error("Confirm button not found");
      tempDiv.remove();
      return;
    }

    confirmBtn.addEventListener("click", () => {
      try {
        bsModal.hide();
        onConfirm();
      } catch (e) {
        console.error("Error in onConfirm:", e);
      }
    });

    modalEl.addEventListener("hidden.bs.modal", () => {
      tempDiv.remove();
    });

    bsModal.show();
  } catch (e) {
    console.error("Error in showConfirmModal:", e);
  }
}

function showModalWithForm(html, modalId, formId, submitBtnId, onSubmit) {
  try {
    const container = document.body;
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = html;
    container.appendChild(tempDiv);

    const modalEl = tempDiv.querySelector(`#${modalId}`);
    const form = tempDiv.querySelector(`#${formId}`);
    const submitBtn = tempDiv.querySelector(`#${submitBtnId}`);

    if (!modalEl) {
      console.error(`Modal element with id ${modalId} not found`);
      tempDiv.remove();
      return;
    }
    
    if (!form) {
      console.error(`Form with id ${formId} not found`);
      tempDiv.remove();
      return;
    }
    
    if (!submitBtn) {
      console.error(`Submit button with id ${submitBtnId} not found`);
      tempDiv.remove();
      return;
    }

    const bsModal = new window.bootstrap.Modal(modalEl);

    submitBtn.addEventListener("click", (e) => {
      try {
        e.preventDefault();
        e.stopPropagation();
        
        if (!form.checkValidity()) {
          form.reportValidity();
          return;
        }

        const formData = new FormData(form);
        const data = Object.fromEntries(formData);

        // Handle checkbox for active field - convert "on" to true/false
        if (formId.includes("movie")) {
          data.active = formData.has("active") ? true : false;
        }

        // Handle checkbox for room if needed
        if (formId.includes("room")) {
          data.active = formData.has("active") ? true : false;
        }

        // Handle checkbox for genre (isActive field)
        if (formId.includes("genre")) {
          data.isActive = formData.has("isActive") ? true : false;
        }

        console.log("Form data:", data);
        bsModal.hide();
        onSubmit(data);
      } catch (e) {
        console.error("Error in form submit:", e);
      }
    });

    modalEl.addEventListener("hidden.bs.modal", () => {
      tempDiv.remove();
    });

    bsModal.show();
  } catch (e) {
    console.error("Error in showModalWithForm:", e);
  }
}

export function showGenerateSeatsModal(roomId, onSubmit) {
  try {
    const modalId = "generateSeatsModal";
    const html = `
      <div class="modal fade" id="${modalId}" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Tạo ghế cho phòng</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <form class="admin-form" id="generateSeatsForm">
                <div class="mb-3">
                  <label class="form-label">Số hàng</label>
                  <input type="number" class="form-control" name="totalRows" value="8" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Số cột</label>
                  <input type="number" class="form-control" name="totalCols" value="10" required>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-admin btn-outline-light" data-bs-dismiss="modal">Hủy</button>
              <button type="button" class="btn btn-admin btn-admin-success" id="generateSubmitBtn">Tạo</button>
            </div>
          </div>
        </div>
      </div>
    `;

    const container = document.body;
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = html;
    container.appendChild(tempDiv);

    const modalEl = tempDiv.querySelector(`#${modalId}`);
    const form = tempDiv.querySelector("#generateSeatsForm");
    const submitBtn = tempDiv.querySelector("#generateSubmitBtn");

    if (!modalEl || !form || !submitBtn) {
      console.error("Modal elements not found");
      tempDiv.remove();
      return;
    }

    const bsModal = new window.bootstrap.Modal(modalEl);

    submitBtn.addEventListener("click", (e) => {
      try {
        e.preventDefault();
        e.stopPropagation();
        
        const formData = new FormData(form);
        const data = {
          roomId,
          totalRows: Number(formData.get("totalRows")),
          totalCols: Number(formData.get("totalCols"))
        };
        bsModal.hide();
        onSubmit(data);
      } catch (e) {
        console.error("Error in generate seats submit:", e);
      }
    });

    modalEl.addEventListener("hidden.bs.modal", () => {
      tempDiv.remove();
    });

    bsModal.show();
  } catch (e) {
    console.error("Error in showGenerateSeatsModal:", e);
  }
}

export function showGenreModal(defaults, onSubmit) {
  const modalId = "genreModal";
  const html = `
    <div class="modal fade" id="${modalId}" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">${defaults?.id ? 'Sửa thể loại' : 'Thêm thể loại'}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <form class="admin-form" id="genreForm">
              <div class="mb-3">
                <label class="form-label">Tên thể loại</label>
                <input type="text" class="form-control" name="name" value="${defaults?.name ?? ''}" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Mô tả</label>
                <textarea class="form-control" name="description" rows="2">${defaults?.description ?? ''}</textarea>
              </div>
              <div class="mb-3">
                <label class="form-check-label">
                  <input type="checkbox" class="form-check-input" name="isActive" ${defaults?.isActive !== false ? 'checked' : ''}>
                  Kích hoạt
                </label>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-admin btn-outline-light" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-admin btn-admin-primary" id="submitBtn">Lưu</button>
          </div>
        </div>
      </div>
    </div>
  `;

  showModalWithForm(html, modalId, "genreForm", "submitBtn", onSubmit);
}
